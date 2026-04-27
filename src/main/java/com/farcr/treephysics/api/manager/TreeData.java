package com.farcr.treephysics.api.manager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TreeData {
    public static final Codec<TreeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("sub_level_id").forGetter(o -> o.subLevelId),
            Codec.INT.fieldOf("life_ticks").forGetter(o -> o.lifeTicks),
            Codec.INT.fieldOf("leaf_break_progress").forGetter(o -> o.leafBreakProgress)
    ).apply(instance, TreeData::new));

    public final UUID subLevelId;
    public int lifeTicks;
    public int leafBreakProgress;

    public TreeData(UUID subLevelId, int lifeTicks, int leafBreakProgress) {
        this.subLevelId = subLevelId;
        this.lifeTicks = lifeTicks;
        this.leafBreakProgress = leafBreakProgress;
    }

    public TreeData(UUID subLevelId) {
        this(subLevelId, 0, 1);
    }

    public TreeData copy(TreeData data) {
        this.lifeTicks = data.lifeTicks;
        this.leafBreakProgress = data.leafBreakProgress;
        return this;
    }

    public @Nullable SubLevel getSubLevel(Level level) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        return container.getSubLevel(this.subLevelId);
    }

    @Override
    public String toString() {
        return "TreeData{" +
                "subLevelId=" + subLevelId +
                ", lifeTicks=" + lifeTicks +
                ", leafBreakProgress=" + leafBreakProgress +
                '}';
    }
}
