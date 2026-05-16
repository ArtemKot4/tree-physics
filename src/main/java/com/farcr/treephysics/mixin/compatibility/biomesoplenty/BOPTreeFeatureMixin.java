package com.farcr.treephysics.mixin.compatibility.biomesoplenty;

import biomesoplenty.worldgen.feature.configurations.BOPTreeConfiguration;
import biomesoplenty.worldgen.feature.tree.BOPTreeFeature;
import com.farcr.treephysics.index.TreePhysicsTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.BiConsumer;

@Mixin(BOPTreeFeature.class)
public class BOPTreeFeatureMixin {

    @WrapMethod(method = "placeLog(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;Ljava/util/function/BiConsumer;Lbiomesoplenty/worldgen/feature/configurations/BOPTreeConfiguration;)Z")
    private <FC extends BOPTreeConfiguration> boolean  treephysics$placeLog(LevelAccessor level, BlockPos pos, Direction.Axis axis, BiConsumer<BlockPos, BlockState> logs, FC config, Operation<Boolean> original) {
        BlockState belowState = level.getBlockState(pos.below());
        if(belowState.is(TreePhysicsTags.CAN_BE_ROOTS)) {
            level.setBlock(pos.below(), Blocks.ROOTED_DIRT.defaultBlockState(), 19);
        }
        return original.call(level, pos, axis, logs, config);
    }

}
