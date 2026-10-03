package com.example.spdim.core.interfaces;

import net.minecraft.nbt.CompoundTag;

public interface CodecToNBT<T> {
    CompoundTag encode(T value);
    T decode(CompoundTag tag);
}
