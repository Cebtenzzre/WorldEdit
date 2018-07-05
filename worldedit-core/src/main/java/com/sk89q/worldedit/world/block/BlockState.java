/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.world.block;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.sk89q.worldedit.world.registry.state.State;
import com.sk89q.worldedit.world.registry.state.value.StateValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An immutable class that represents the state a block can be in.
 */
@SuppressWarnings("unchecked")
public class BlockState implements BlockStateHolder<BlockState> {

    private final BlockType blockType;
    private final Map<State, StateValue> values;
    private final boolean fuzzy;

    // Neighbouring state table.
    private Table<State, StateValue, BlockState> states;

    BlockState(BlockType blockType) {
        this.blockType = blockType;
        this.values = new HashMap<>();
        this.fuzzy = false;
    }

    /**
     * Creates a fuzzy BlockState. This can be used for partial matching.
     *
     * @param blockType The block type
     * @param values The block state values
     */
    public BlockState(BlockType blockType, Map<State, StateValue> values) {
        this.blockType = blockType;
        this.values = values;
        this.fuzzy = true;
    }

    public void populate(Map<Map<State, StateValue>, BlockState> stateMap) {
        final Table<State, StateValue, BlockState> states = HashBasedTable.create();

        for(final Map.Entry<State, StateValue> entry : this.values.entrySet()) {
            final State state = entry.getKey();

            state.getValues().forEach(value -> {
                if(value != entry.getValue()) {
                    states.put(state, (StateValue) value, stateMap.get(this.withValue(state, (StateValue) value)));
                }
            });
        }

        this.states = states.isEmpty() ? states : ArrayTable.create(states);
    }

    private Map<State, StateValue> withValue(final State property, final StateValue value) {
        final Map<State, StateValue> values = Maps.newHashMap(this.values);
        values.put(property, value);
        return values;
    }

    @Override
    public BlockType getBlockType() {
        return this.blockType;
    }

    @Override
    public BlockState with(State state, StateValue value) {
        if (fuzzy) {
            return setState(state, value);
        } else {
            BlockState result = states.get(state, value);
            return result == null ? this : result;
        }
    }

    @Override
    public StateValue getState(State state) {
        return this.values.get(state);
    }

    @Override
    public Map<State, StateValue> getStates() {
        return Collections.unmodifiableMap(this.values);
    }

    public BlockState toFuzzy() {
        return new BlockState(this.getBlockType(), new HashMap<>());
    }

    @Override
    public boolean equalsFuzzy(BlockStateHolder o) {
        if (!getBlockType().equals(o.getBlockType())) {
            return false;
        }

        List<State> differingStates = new ArrayList<>();
        for (Object state : o.getStates().keySet()) {
            if (getState((State) state) == null) {
                differingStates.add((State) state);
            }
        }
        for (State state : getStates().keySet()) {
            if (o.getState(state) == null) {
                differingStates.add(state);
            }
        }

        for (State state : differingStates) {
            if (!getState(state).equals(o.getState(state))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public BlockState toImmutableState() {
        return this;
    }

    /**
     * Internal method used for creating the initial BlockState.
     *
     * Sets a value. DO NOT USE THIS.
     *
     * @param state The state
     * @param value The value
     * @return The blockstate, for chaining
     */
    private BlockState setState(State state, StateValue value) {
        this.values.put(state, value);
        return this;
    }
}
