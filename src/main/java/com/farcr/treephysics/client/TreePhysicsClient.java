package com.farcr.treephysics.client;

import com.farcr.treephysics.TreePhysics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = TreePhysics.MOD_ID, dist = Dist.CLIENT)
public class TreePhysicsClient {

    public static final TreeClientHandler TREE_HANDLER = new TreeClientHandler();

    public TreePhysicsClient(IEventBus eventBus, ModContainer container) {

    }

}
