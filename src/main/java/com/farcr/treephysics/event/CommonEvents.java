package com.farcr.treephysics.event;

import com.farcr.treephysics.TreePhysics;
import com.farcr.treephysics.api.manager.TreeServerHandler;
import com.farcr.treephysics.api.manager.TreeSubLevelObserver;
import com.farcr.treephysics.api.tree_gathering.TreeGatherer;
import com.farcr.treephysics.client.TreeManager;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = TreePhysics.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        TreeServerHandler.sendUpdatePacket(player);
    }

    @SubscribeEvent
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if(!event.getPlayer().isShiftKeyDown() && event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.AXES)) {
            TreeGatherer.trySplit((ServerLevel) event.getLevel(), event.getPos());
        }
    }

    @SubscribeEvent
    public static void itemUseOnBlock(UseItemOnBlockEvent event) {
        BlockPos pos = event.getPos();
        SubLevel subLevel = Sable.HELPER.getContaining(event.getLevel(), pos);
        TreeManager treeManager = TreeManager.get(event.getLevel());
        Level level = event.getLevel();

        if(subLevel != null && treeManager.isTree(subLevel)) {
            if(event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && event.getItemStack().is(Items.HONEYCOMB)) {
                if(!event.getLevel().isClientSide()) {
                    TreeServerHandler handler = (TreeServerHandler) treeManager;
                    handler.unsetTree(subLevel);
                } else {
                    BoundingBox3ic box = subLevel.getPlot().getBoundingBox();
                    Iterable<BlockPos> iterator = BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1);
                    for (BlockPos blockPos : iterator) {
                        BlockState state = level.getBlockState(blockPos);
                        if(!state.isAir()) {
                            for (Direction direction : Direction.values()) {
                                BlockState relative = level.getBlockState(blockPos.relative(direction));
                                if(relative.isAir()) {
                                    ParticleUtils.spawnParticlesOnBlockFace(level, blockPos, ParticleTypes.WAX_ON, UniformInt.of(1, 2), direction, () -> new Vec3(0, 0, 0), 0.55);
                                }
                            }
                        }
                    }
                }

                event.cancelWithResult(ItemInteractionResult.SUCCESS);
                level.playSound(null, event.getPos(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                event.getItemStack().shrink(1);
            } else {
                event.setCanceled(true);
            }
        }
    }

    public static void containerReady(Level level, SubLevelContainer container) {
        if(!(container instanceof ServerSubLevelContainer serverContainer)) {
            return;
        }

        serverContainer.addObserver(new TreeSubLevelObserver(serverContainer.getLevel()));
    }

    public static void postPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        ServerLevel level = system.getLevel();
        TreeServerHandler handler = TreeServerHandler.get(level);
        PhysicsPipeline pipeline = system.getPipeline();
        handler.physicsTick(level, system, pipeline, timeStep);
    }
}
