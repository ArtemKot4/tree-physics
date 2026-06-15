package com.farcr.treephysics.mixin.walk_through_leaves;

import com.farcr.treephysics.index.TreePhysicsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CanFallAtleastHelper.class)
public class CanFallAtLeastHelperMixin {

    @WrapOperation(method = "canFallAtleastWithSubLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private static VoxelShape treephysics$getCollisionShape(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<VoxelShape> original) {
        if(instance.getBlock() instanceof LeavesBlock && TreePhysicsConfig.LEAF_WALKING_BEHAVIOR.get().allowSubLevel()) {
            return Shapes.empty();
        }
        return original.call(instance, blockGetter, pos);
    }

}
