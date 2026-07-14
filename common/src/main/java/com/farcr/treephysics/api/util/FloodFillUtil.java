package com.farcr.treephysics.api.util;

import com.farcr.treephysics.api.flood_fill.TreeFloodFill;
import com.farcr.treephysics.api.flood_fill.TreeResult;
import com.farcr.treephysics.api.manager.ServerTreeManager;
import com.farcr.treephysics.api.manager.TreeManager;
import com.farcr.treephysics.index.TreePhysicsConfig;
import com.farcr.treephysics.index.TreePhysicsTags;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FloodFillUtil {
    public static final BlockPos[] DIRECTION_OFFSETS_CORNERS = new BlockPos[] {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(0, 1, 1),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, 1, 1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, -1),
            new BlockPos(-1, 1, 1),
            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1, -1, -1),
            new BlockPos(-1, -1, 1)
    };

    private static final TreeFloodFill TREE_VALIDATOR = new TreeFloodFill()
            .addRule(FloodFillUtil::logRule)
            .setEarlyReturn(TreeResult::hasRoot);

    private static final TreeFloodFill ROOTLESS_TREE_VALIDATOR = new TreeFloodFill()
            .addRule(FloodFillUtil::logRule)
            .addRule(FloodFillUtil::leafRule)
            .setEarlyReturn(result -> result.hasRoot() || (result.hasDirt() && result.hasLeaves()));

    private static final TreeFloodFill TREE_FINDER = new TreeFloodFill()
            .addRule(FloodFillUtil::logRule)
            .addRule(FloodFillUtil::leafRule)
            .addRule(FloodFillUtil::attachmentRule)
            .addRule(FloodFillUtil::fallingBlockRule)
            .addTag(TreePhysicsTags.TREE)
            .addTag(TreePhysicsTags.FALLS_FROM_TREES);

    public static boolean isValidTree(BlockGetter blockGetter, BlockPos pos) {
        boolean rootless = TreePhysicsConfig.ROOTLESS_TREE_DETECTION.getAsBoolean();
        TreeResult tree = (rootless ? ROOTLESS_TREE_VALIDATOR : TREE_VALIDATOR).findBlocks(blockGetter, pos);
        if(tree != null) {
            //level.players().get(0).sendSystemMessage(Component.literal("дерево при помощи findBlocks нашлось, использован валидатор: " + tree.getClass().getName() + (", листья? " + tree.hasLeaves()) + (", корни? " + tree.hasRoot()) + ", главное условие? " + (tree.hasRoot() || (rootless && tree.hasLeaves()/*&& tree.hasDirt()*/))));
            return tree.hasRoot() || (rootless && tree.hasLeaves()/*&& tree.hasDirt()*/);
        }
        //level.players().get(0).sendSystemMessage(Component.literal("дерево в findBlocks не нашлось"));
        return false;
    }

    public static List<ServerSubLevel> trySplit(ServerLevel level, BlockPos pos) {
        if(!isValidTree(level, pos)) {
            //level.getRandomPlayer().sendSystemMessage(Component.literal("провалено: не действительное дерево")); //debug
            return List.of();
        }
        //level.getRandomPlayer().sendSystemMessage(Component.literal("Ис валид три прошло, начинаю сборку!"));
        TreeFloodFill floodFill = TREE_FINDER.ignore(pos);

        List<ServerSubLevel> subLevels = new ArrayList<>();
        ServerTreeManager manager = (ServerTreeManager) TreeManager.get(level);

        for (BlockPos offset : DIRECTION_OFFSETS_CORNERS) {
            BlockPos start = pos.offset(offset);

            TreeResult tree = floodFill.findBlocks(level, start);

            if(tree != null && !(TreePhysicsConfig.ROOTLESS_TREE_DETECTION.get() ? tree.hasRoot() || tree.hasDirt() : tree.hasRoot())) {
                Set<BlockPos> treeBlocks = tree.getBlocks(TreePhysicsTags.TREE);
                ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, pos, treeBlocks, new BoundingBox3i(pos, pos));
                subLevels.add(subLevel);
                manager.setTree(subLevel);

                Set<BlockPos> fallingBlocks = tree.getBlocks(TreePhysicsTags.FALLS_FROM_TREES);
                for (BlockPos blockPos : fallingBlocks) {
                    BlockState state = level.getBlockState(blockPos);
                    if (PhysicsBlockPropertyHelper.getMass(level, pos, state) > 0.0) {
                        SubLevelAssemblyHelper.assembleBlocks(level, blockPos, List.of(blockPos), new BoundingBox3i(blockPos, blockPos));
                    }
                }

                for (BlockPos blockPos : tree.getBlocks()) {
                    level.setBlock(blockPos, Blocks.BARRIER.defaultBlockState(), 2);
                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
                }
            }

        }
        return subLevels;
    }

    public static boolean logRule(BlockPos fromPos, BlockPos toPos, BlockState fromState, BlockState toState, TreeResult result) {
        return TreeUtil.isLog(fromState) && TreeUtil.isLog(toState);
    }

    public static boolean leafRule(BlockPos fromPos, BlockPos toPos, BlockState fromState, BlockState toState, TreeResult result) {
        if(TreeUtil.isLog(fromState) && TreeUtil.isLeaf(toState)) {
            return true;
        }

        if(TreeUtil.isLeaf(fromState) && TreeUtil.isLeaf(toState)) {
            if(TreeUtil.isSameLeafType(fromState, toState)) {
                int fromDistance = TreeUtil.getLeafDistance(fromState, fromPos, result);
                int toDistance = TreeUtil.getLeafDistance(toState, toPos, result);
                return toDistance > fromDistance;
            }
        }

        return false;
    }

    public static boolean attachmentRule(BlockPos fromPos, BlockPos toPos, BlockState fromState, BlockState toState, TreeResult result) {
        return !fromState.is(TreePhysicsTags.STAYS_ON_TREE) && toState.is(TreePhysicsTags.STAYS_ON_TREE);
    }

    public static boolean fallingBlockRule(BlockPos fromPos, BlockPos toPos, BlockState fromState, BlockState toState, TreeResult result) {
        return !fromState.is(TreePhysicsTags.FALLS_FROM_TREES) && toState.is(TreePhysicsTags.FALLS_FROM_TREES);
    }
}
