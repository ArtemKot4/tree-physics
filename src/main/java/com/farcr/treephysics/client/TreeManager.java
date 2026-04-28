package com.farcr.treephysics.client;

import com.farcr.treephysics.api.manager.TreeServerHandler;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface TreeManager {
    boolean isTree(@Nullable SubLevel subLevel);

    Level getLevel();

    default boolean isTree(BlockPos pos) {
        SubLevel subLevel = Sable.HELPER.getContaining(getLevel(), pos);
        return isTree(subLevel);
    }

    static TreeManager get(Level level) {
        if(level.isClientSide()) {
            return TreePhysicsClient.TREE_HANDLER;
        } else {
            return TreeServerHandler.get((ServerLevel) level);
        }
    }
}
