package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;

public class BooleanCodec implements CodecToNBT<Boolean> {

	@Override
	public CompoundTag encode(Boolean value) {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("Value", value);
		return tag;
	}

	@Override
	public Boolean decode(CompoundTag tag) {
		return tag.getBoolean("Value");
	}
}
