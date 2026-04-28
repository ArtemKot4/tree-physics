package com.farcr.treephysics.client;

import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TreeClientHandler implements TreeManager {
    private final Map<ResourceKey<Level>, Set<UUID>> trees = new Object2ObjectOpenHashMap<>();

    @Override
    public boolean isTree(SubLevel subLevel) {
        Set<UUID> trees = this.trees.get(subLevel.getLevel().dimension());
        return trees.contains(subLevel.getUniqueId());
    }

    public void setTrees(ResourceKey<Level> dimension, Set<UUID> trees) {
        this.trees.put(dimension, trees);
    }
}
