package com.farcr.treephysics.client;

import com.farcr.treephysics.api.manager.TreeServerHandler;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface TreeManager {
    boolean isTree(SubLevel subLevel);

    static TreeManager get(Level level) {
        if(level.isClientSide()) {
            return TreePhysicsClient.TREE_HANDLER;
        } else {
            return TreeServerHandler.get((ServerLevel) level);
        }
    }
}
