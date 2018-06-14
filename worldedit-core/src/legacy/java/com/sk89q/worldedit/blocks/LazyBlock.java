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

package com.sk89q.worldedit.blocks;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.type.BlockType;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.registry.state.State;
import com.sk89q.worldedit.world.registry.state.value.StateValue;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

/**
 * A implementation of a lazy block for {@link Extent#getLazyBlock(Vector)}
 * that takes the block's ID and metadata, but will defer loading of NBT
 * data until time of access.
 *
 * <p>NBT data is later loaded using a call to {@link Extent#getBlock(Vector)}
 * with a stored {@link Extent} and location.</p>
 *
 * <p>All mutators on this object will throw an
 * {@link UnsupportedOperationException}.</p>
 */
public class LazyBlock extends BaseBlock {

    private final Extent extent;
    private final Vector position;
    private boolean loaded = false;

    /**
     * Create a new lazy block.
     *
     * @param type the block type
     * @param extent the extent to later load the full block data from
     * @param position the position to later load the full block data from
     */
    public LazyBlock(BlockType type, Extent extent, Vector position) {
        super(type);
        checkNotNull(extent);
        checkNotNull(position);
        this.extent = extent;
        this.position = position;
    }

    /**
     * Create a new lazy block.
     *
     * @param type the block type
     * @param extent the extent to later load the full block data from
     * @param position the position to later load the full block data from
     */
    @Deprecated
    public LazyBlock(int type, Extent extent, Vector position) {
        super(type);
        checkNotNull(extent);
        checkNotNull(position);
        this.extent = extent;
        this.position = position;
    }

    /**
     * Create a new lazy block.
     *
     * @param type the block type
     * @param states the block states
     * @param extent the extent to later load the full block data from
     * @param position the position to later load the full block data from
     */
    @Deprecated
    public LazyBlock(BlockType type, Map<State, StateValue> states, Extent extent, Vector position) {
        super(type, states);
        checkNotNull(extent);
        checkNotNull(position);
        this.extent = extent;
        this.position = position;
    }

    /**
     * Create a new lazy block.
     *
     * @param type the block type
     * @param data the data value
     * @param extent the extent to later load the full block data from
     * @param position the position to later load the full block data from
     */
    @Deprecated
    public LazyBlock(int type, int data, Extent extent, Vector position) {
        super(type, data);
        checkNotNull(extent);
        checkNotNull(position);
        this.extent = extent;
        this.position = position;
    }

    @Override
    public void setId(int id) {
        throw new UnsupportedOperationException("This object is immutable");
    }

    @Override
    public void setData(int data) {
        throw new UnsupportedOperationException("This object is immutable");
    }

    @Override
    public void setType(BlockType type) {
        throw new UnsupportedOperationException("This object is immutable");
    }

    @Override
    public void setState(State state, StateValue stateValue) {
        throw new UnsupportedOperationException("This object is immutable");
    }

    @Override
    public CompoundTag getNbtData() {
        if (!loaded) {
            BaseBlock loadedBlock = extent.getBlock(position);
            super.setNbtData(loadedBlock.getNbtData());
        }
        return super.getNbtData();
    }

    @Override
    public void setNbtData(CompoundTag nbtData) {
        throw new UnsupportedOperationException("This object is immutable");
    }

}
