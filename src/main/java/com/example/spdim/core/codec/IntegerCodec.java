package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;

public class IntegerCodec implements CodecToNBT<Integer> {

    @Override
    public CompoundTag encode(Integer value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Value", value);
        return tag;
    }

    @Override
    public Integer decode(CompoundTag tag) {
        return tag.getInt("Value");
    }
}
