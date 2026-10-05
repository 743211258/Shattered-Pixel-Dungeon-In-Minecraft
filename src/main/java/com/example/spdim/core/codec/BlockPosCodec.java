package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class BlockPosCodec implements CodecToNBT<BlockPos> {

	@Override
	public CompoundTag encode(BlockPos value) {
		CompoundTag tag = new CompoundTag();
		tag.putLong("Pos", value.asLong());
		return tag;
	}

	@Override
	public BlockPos decode(CompoundTag tag) {
		return BlockPos.of(tag.getLong("Pos"));
	}
}
