package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;

public class FloatCodec implements CodecToNBT<Float> {

	@Override
	public CompoundTag encode(Float value) {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("Value", value);
		return tag;
	}

	@Override
	public Float decode(CompoundTag tag) {
		return tag.getFloat("Value");
	}
}
