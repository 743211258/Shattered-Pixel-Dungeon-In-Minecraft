package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class UUIDCodec implements CodecToNBT<UUID> {
	@Override
	public CompoundTag encode(UUID value) {
		CompoundTag tag = new CompoundTag();
		tag.putUUID("UUID", value);
		return tag;
	}

	@Override
	public UUID decode(CompoundTag tag) {
		return tag.getUUID("UUID");
	}
}
