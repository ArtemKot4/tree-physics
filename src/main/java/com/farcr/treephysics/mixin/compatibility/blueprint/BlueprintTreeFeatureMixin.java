package com.farcr.treephysics.mixin.compatibility.blueprint;

import com.farcr.treephysics.index.TreePhysicsConfig;
import com.farcr.treephysics.index.TreePhysicsTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamabnormals.blueprint.common.levelgen.feature.BlueprintTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlueprintTreeFeature.class)
public class BlueprintTreeFeatureMixin {

    @WrapOperation(method = "setDirtAt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;forceDirt:Z", opcode = Opcodes.GETFIELD))
    private static boolean treephysics$forceDirt(TreeConfiguration instance, Operation<Boolean> original, @Local(argsOnly = true) WorldGenLevel level, @Local(argsOnly = true) BlockPos pos) {
        return original.call(instance) || level.getBlockState(pos).is(TreePhysicsTags.CAN_BE_ROOTS);
    }

    @WrapOperation(method = "setDirtAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean treephysics$setDirtAt(WorldGenLevel instance, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
        if(TreePhysicsConfig.ROOTED_DIRT_GENERATION.get()) {
            state = Blocks.ROOTED_DIRT.defaultBlockState();
        }
        return original.call(instance, pos, state, i);
    }

}
