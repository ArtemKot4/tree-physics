package com.farcr.treephysics.mixin.walk_through_leaves;

import com.farcr.treephysics.api.util.TreeUtil;
import com.farcr.treephysics.index.TreePhysicsConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow
    protected abstract BlockState asState();

    @WrapMethod(method = "isSuffocating")
    private boolean treephysics$isSuffocating(BlockGetter level, BlockPos pos, Operation<Boolean> original) {
        if(TreeUtil.isLeaf(this.asState()) && TreePhysicsConfig.LEAF_WALKING_BEHAVIOR.get() != TreePhysicsConfig.LeafWalkingBehavior.NEVER) {
            return false;
        }

        return original.call(level, pos);
    }

    @WrapMethod(method = "isViewBlocking")
    private boolean treephysics$isViewBlocking(BlockGetter level, BlockPos pos, Operation<Boolean> original) {
        if(TreeUtil.isLeaf(this.asState()) && TreePhysicsConfig.LEAF_WALKING_BEHAVIOR.get() != TreePhysicsConfig.LeafWalkingBehavior.NEVER) {
            return false;
        }

        return original.call(level, pos);
    }

    @WrapMethod(method = "getVisualShape")
    private VoxelShape treephysics$getVisualShape(BlockGetter level, BlockPos pos, CollisionContext context, Operation<VoxelShape> original) {
        if(TreeUtil.isLeaf(this.asState()) && TreePhysicsConfig.LEAF_WALKING_BEHAVIOR.get() != TreePhysicsConfig.LeafWalkingBehavior.NEVER) {
            return Shapes.empty();
        }

        return original.call(level, pos, context);
    }

}
