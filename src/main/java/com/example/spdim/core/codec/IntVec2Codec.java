package com.example.spdim.core.codec;

import com.example.spdim.core.data_structure.IntVec2;
import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;

public class IntVec2Codec implements CodecToNBT<IntVec2> {

    @Override
    public CompoundTag encode(IntVec2 value) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("X", value.getX());
        tag.putInt("Y", value.getY());

        return tag;
    }

    @Override
    public IntVec2 decode(CompoundTag tag) {
        return new IntVec2(
            tag.getInt("X"),
            tag.getInt("Y")
        );
    }
}
