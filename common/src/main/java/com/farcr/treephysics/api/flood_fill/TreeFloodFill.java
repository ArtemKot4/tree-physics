package com.farcr.treephysics.api.flood_fill;

import com.farcr.treephysics.api.util.FloodFillUtil;
import com.farcr.treephysics.api.util.TreeUtil;
import com.farcr.treephysics.index.TreePhysicsTags;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;
import java.util.function.Predicate;

public class TreeFloodFill {
    private final List<Rule> rules = new ArrayList<>();
    private final Set<TagKey<Block>> tags = new HashSet<>();
    private Predicate<TreeResult> earlyReturn = null;
    private BlockPos ignorePos = null;

    public TreeFloodFill addRule(Rule rule) {
        this.rules.add(rule);
        return this;
    }

    public TreeFloodFill addTag(TagKey<Block> tag) {
        this.tags.add(tag);
        return this;
    }

    public TreeFloodFill setEarlyReturn(Predicate<TreeResult> earlyReturn) {
        this.earlyReturn = earlyReturn;
        return this;
    }

    public TreeFloodFill ignore(BlockPos pos) {
        this.ignorePos = pos;
        return this;
    }

    public TreeResult findBlocks(BlockGetter blockGetter, BlockPos start, ServerLevel level) {
        if(!TreeUtil.isLog(blockGetter.getBlockState(start))) {
            //level.players().get(0).sendSystemMessage(Component.literal("Завершено: не бревно " + start.getX() + " , " +  start.getY() + " , " +  start.getZ() + ", там " + level.getBlockState(start).getBlock().getDescriptionId()));
            return null;
        }

        TreeResult result = new TreeResult(this.tags, start);
        Queue<BlockPos> queue = new LinkedList<>();
        Set<Long> visited = new LongOpenHashSet();

        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos centerPos = queue.poll();
            BlockState centerState = blockGetter.getBlockState(centerPos);

            visited.add(centerPos.asLong());

            if(!this.shouldIgnore(centerPos)) {
                if(centerState.is(TreePhysicsTags.LEAVES)) {
                    result.leaves = true; //обходим лимиты, хы-хы)
                    if(centerState.getValue(BlockStateProperties.PERSISTENT) == true) {
                        centerState = centerState.setValue(BlockStateProperties.PERSISTENT, false);
                        level.setBlock(centerPos, centerState, 2);
                        //level.getRandomPlayer().sendSystemMessage(Component.literal("блокстейт обновлён"));
                    } //else {
                        //level.getRandomPlayer().sendSystemMessage(Component.literal("листва и так должна опадать"));
                    //}
                    //level.getRandomPlayer().sendSystemMessage(Component.literal("дистанция " + centerState.getValue(BlockStateProperties.DISTANCE)));
                }
                result.add(centerPos, centerState);
                result.afterSpread(blockGetter, centerPos, centerState);
            }

            if(this.earlyReturn != null && this.earlyReturn.test(result)) {
                break;
            }

            for (BlockPos offset : FloodFillUtil.DIRECTION_OFFSETS_CORNERS) {
                BlockPos nextPos = centerPos.offset(offset);
                if(visited.contains(nextPos.asLong())) {
                    continue;
                }

                BlockState nextState = blockGetter.getBlockState(nextPos);
                if(nextState.isAir()) {
                    continue;
                }

                for (Rule rule : this.rules) {
                    if(this.shouldIgnore(nextPos)) {
                        continue;
                    }

                    if(rule.canSpread(centerPos, nextPos, centerState, nextState, result)) {
                        queue.add(nextPos);
                        visited.add(nextPos.asLong());
                        break;
                    }
                }
            }
        }
        //level.players().get(0).sendSystemMessage(Component.literal("Завершено: собрано блоков " + result.getBlocks().size() ));
        return result;
    }

    private boolean shouldIgnore(BlockPos pos) {
        return this.ignorePos != null && this.ignorePos.equals(pos);
    }

}
