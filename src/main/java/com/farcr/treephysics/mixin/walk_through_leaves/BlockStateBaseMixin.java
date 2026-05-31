package com.farcr.treephysics.mixin.walk_through_leaves;

import com.farcr.treephysics.api.util.TreeUtil;
import com.farcr.treephysics.index.TreePhysicsConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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

}
