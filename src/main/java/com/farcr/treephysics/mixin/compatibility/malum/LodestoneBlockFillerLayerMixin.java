package com.farcr.treephysics.mixin.compatibility.malum;

import com.farcr.treephysics.api.util.TreeUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.systems.worldgen.LodestoneBlockFiller;

@SuppressWarnings("removal")
@Mixin(LodestoneBlockFiller.class)
public class LodestoneBlockFillerLayerMixin {

    @WrapOperation(method = "lambda$fill$2", at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/worldgen/LodestoneBlockFiller$BlockStateEntry;place(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)V"))
    private static void treephysics$fill(LodestoneBlockFiller.BlockStateEntry instance, LevelAccessor level, BlockPos pos, Operation<Void> original) {
        original.call(instance, level, pos);
        if(TreeUtil.getLogAxis(instance.getState()) == Direction.Axis.Y) {
            BlockPos below = pos.below();
            if(TreeUtil.canBeRoots(level, below)) {
                BlockState rootState = TreeUtil.getRootForState(level.getBlockState(below));
                level.setBlock(below, rootState, 19);
            }
        }
    }


}
