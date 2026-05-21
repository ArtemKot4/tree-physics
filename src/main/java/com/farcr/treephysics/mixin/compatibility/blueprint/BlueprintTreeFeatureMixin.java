package com.farcr.treephysics.mixin.compatibility.blueprint;

import com.farcr.treephysics.index.TreePhysicsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.blueprint.common.levelgen.feature.BlueprintTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlueprintTreeFeature.class)
public class BlueprintTreeFeatureMixin {

    @WrapOperation(method = "setDirtAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean treephysics$setDirtAt(WorldGenLevel instance, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        if(TreePhysicsConfig.ROOTED_DIRT_GENERATION.get()) {
            state = Blocks.ROOTED_DIRT.defaultBlockState();
        }
        return original.call(instance, pos, state, i);
    }

}
