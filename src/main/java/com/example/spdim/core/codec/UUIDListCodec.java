package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDListCodec implements CodecToNBT<List<UUID>> {

	private final UUIDCodec uuidCodec = new UUIDCodec();

	@Override
	public CompoundTag encode(List<UUID> value) {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();

		for (UUID uuid : value) {
			list.add(uuidCodec.encode(uuid));
		}

		tag.put("UUIDs", list);
		return tag;
	}

	@Override
	public List<UUID> decode(CompoundTag tag) {
		List<UUID> value = new ArrayList<>();

		ListTag list = tag.getList("UUIDs", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			value.add(uuidCodec.decode(list.getCompound(i)));
		}

		return value;
	}
}
