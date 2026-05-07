package com.farcr.treephysics.index;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TreePhysicsConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue DESPAWN_TIME;
    public static final ModConfigSpec.EnumValue<DespawnBehavior> DESPAWN_BEHAVIOR;
    public static final ModConfigSpec.BooleanValue CAN_BUILD;
    public static final ModConfigSpec.BooleanValue CAN_WALK_THROUGH_LEAVES;
    public static final ModConfigSpec.DoubleValue LEAF_WALKING_SPEED;
    public static final ModConfigSpec.DoubleValue TREE_ENTITY_DAMAGE;

    public static final ModConfigSpec.DoubleValue GRAVITY_MULTIPLIER;
    public static final ModConfigSpec.IntValue GRAVITY_MULTIPLIER_TICKS;
    public static final ModConfigSpec.DoubleValue IMPULSE_FORCE;
    public static final ModConfigSpec.DoubleValue IMPULSE_TORQUE;
    public static final ModConfigSpec.DoubleValue EXTRA_PUSH_MULTIPLIER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        DESPAWN_TIME = builder
                .comment("The amount of time in ticks a tree will exist before despawning")
                .defineInRange("despawn_time", 144000, 0, Integer.MAX_VALUE);

        DESPAWN_BEHAVIOR = builder
                .comment(
                        "NO_DESPAWN: Trees will not despawn at all",
                        "DESPAWN_SMALL: Trees with 5 or less logs will despawn",
                        "DESPAWN_ALL: Every tree will despawn"
                )
                .defineEnum("despawn_behavior", DespawnBehavior.DESPAWN_SMALL);

        CAN_BUILD = builder
                .comment("If trees can be built on")
                .define("can_build", false);

        CAN_WALK_THROUGH_LEAVES = builder
                .comment("If entities should be able to walk through leaves")
                .define("can_walk_through_leaves", true);

        LEAF_WALKING_SPEED = builder
                .comment("Multiplier for entity walking speed when in leaves")
                .defineInRange("leaf_walking_speed", 0.67, 0.0, 1.0);

        TREE_ENTITY_DAMAGE = builder
                .comment("How much damage a falling tree should inflict on an entity when moving at 1 block per tick")
                .defineInRange("tree_entity_damage", 25, 0.0, Double.MAX_VALUE);

        builder.push("physics");

        GRAVITY_MULTIPLIER = builder
                .comment("How much extra gravity should be applied to trees")
                .defineInRange("gravity_multiplier", 1.0, 1.0, Double.MAX_VALUE);

        GRAVITY_MULTIPLIER_TICKS = builder
                .comment("How long in ticks the gravity multiplier should be applied. -1 for infinite")
                .defineInRange("gravity_multiplier_ticks", 400, -1, Integer.MAX_VALUE);

        IMPULSE_FORCE = builder
                .comment("How much force should be applied to trees when chopped down")
                .defineInRange("impulse_force", 1.5, 0.0, Double.MAX_VALUE);

        IMPULSE_TORQUE = builder
                .comment("How much torque should be applied to trees when chopped down")
                .defineInRange("impulse_torque", 0.3, 0.0, Double.MAX_VALUE);

        EXTRA_PUSH_MULTIPLIER = builder
                .comment("How much extra pushing strength should be applied for upright trees")
                .defineInRange("extra_push_multiplier", 1.5, 0.0, Double.MAX_VALUE);

        builder.pop();

        SPEC = builder.build();
    }

    public enum DespawnBehavior {
        NO_DESPAWN,
        DESPAWN_SMALL,
        DESPAWN_ALL
    }

}
