package com.farcr.treephysics.networking;

import com.farcr.treephysics.TreePhysics;
import foundry.veil.api.network.VeilPacketManager;

public class TreePhysicsPackets {
    public static final VeilPacketManager INSTANCE = VeilPacketManager.create(TreePhysics.MOD_ID, "1.0");

    public static void init() {
        INSTANCE.registerClientbound(UpdateClientTrees.TYPE, UpdateClientTrees.CODEC, UpdateClientTrees::handle);
    }
}
