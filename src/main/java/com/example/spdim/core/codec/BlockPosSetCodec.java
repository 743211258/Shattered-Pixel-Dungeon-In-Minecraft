package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;
import com.example.spdim.core.codec.BlockPosCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

public class BlockPosSetCodec implements CodecToNBT<Set<BlockPos>> {

	private final BlockPosCodec blockPosCodec = new BlockPosCodec();

	@Override
	public CompoundTag encode(Set<BlockPos> value) {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();

		for (BlockPos pos : value) {
			list.add(blockPosCodec.encode(pos));
		}

		tag.put("Blocks", list);

		return tag;
	}

	@Override
	public Set<BlockPos> decode(CompoundTag tag) {
		Set<BlockPos> value = new HashSet<>();

		ListTag list = tag.getList("Blocks", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			value.add(
				blockPosCodec.decode(list.getCompound(i))
			);
		}

		return value;
	}
}
