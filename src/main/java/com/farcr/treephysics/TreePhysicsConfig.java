package com.farcr.treephysics;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TreePhysicsConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue MAX_LIFE_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        MAX_LIFE_TICKS = builder
                .comment("The amount of time in ticks a tree will exist before despawning. -1 will disable this.")
                .defineInRange("max_life_ticks", 144000, -1, Integer.MAX_VALUE);

        SPEC = builder.build();
    }

}
