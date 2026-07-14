package com.farcr.treephysics.neoforge.mixin.compatibility.malum;

import com.farcr.treephysics.api.util.TreeUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.sammy.malum.common.worldgen.tree.SoulwoodTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.systems.worldgen.LodestoneWorldgenBuilder;
import team.lodestar.lodestone.systems.worldgen.LodestoneWorldgenBuilderEntry;
import team.lodestar.lodestone.systems.worldgen.LodestoneWorldgenBuilderLayer;

@Mixin(SoulwoodTreeFeature.class)
public class SoulwoodTreeFeatureMixin {

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/worldgen/LodestoneWorldgenBuilder;place(Lnet/minecraft/world/level/WorldGenLevel;)V", ordinal = 0))
    private void treephysics$place(LodestoneWorldgenBuilder instance, WorldGenLevel level, Operation<Void> original, @Local(name = "treeLayer") LodestoneWorldgenBuilderLayer treeLayer) {
        original.call(instance, level);
        for (LodestoneWorldgenBuilderEntry entry : treeLayer.getEntries()) {
            BlockState entryState = entry.blockState();
            BlockPos entryPos = entry.position();
            BlockPos belowPos = entryPos.below();
            if(/*TreeUtil.getLogAxis(entryState) == Direction.Axis.Y &&*/TreeUtil.canBeRoots(level, belowPos)) {
                BlockState rootState = TreeUtil.getRootForState(level.getBlockState(belowPos));
                level.setBlock(belowPos, rootState, 19);
            }
        }
    }
}
