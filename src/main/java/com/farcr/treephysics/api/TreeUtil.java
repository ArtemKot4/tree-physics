package com.farcr.treephysics.api;

import com.farcr.treephysics.index.TreePhysicsTags;
import com.farcr.treephysics.api.tree_gathering.TreeContext;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class TreeUtil {
    public static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[] {
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
            new BlockPos(0, 1, -1)
    };

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

    private static final Vector3d DIRECTION = new Vector3d();
    private static final Vector3dc UP = new Vector3d(0, 1, 0);

    public static double getUprightness(SubLevel subLevel) {
        Vector3d direction = subLevel.logicalPose().transformNormal(DIRECTION.set(UP));
        double value = Math.max(0, direction.dot(UP));
        return value;
    }

    public static boolean treeSpread(BlockState from, BlockState to, TreeContext context) {
        if(context.isLog(from)) {
            return context.isLog(to) || context.isLeaf(to) || to.is(TreePhysicsTags.STAYS_ON_TREE);
        }

        if(context.isLeaf(from)) {
            if(context.isLeaf(to)) {
                int fromDistance = from.getValue(LeavesBlock.DISTANCE);
                int toDistance = to.getValue(LeavesBlock.DISTANCE);
                return fromDistance < toDistance;
            } else {
                return to.is(TreePhysicsTags.STAYS_ON_TREE);
            }
        }

        return false;
    }

    public static boolean logSpread(BlockState from, BlockState to, TreeContext context) {
        return context.isLog(from) && context.isLog(to);
    }
}
