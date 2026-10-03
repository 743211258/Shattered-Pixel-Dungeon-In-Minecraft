package com.example.spdim.core.codec;

import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class Vec3Codec implements CodecToNBT<Vec3> {

	@Override
	public CompoundTag encode(Vec3 value) {
		CompoundTag tag = new CompoundTag();

		tag.putDouble("X", value.x);
		tag.putDouble("Y", value.y);
		tag.putDouble("Z", value.z);

		return tag;
	}

	@Override
	public Vec3 decode(CompoundTag tag) {
		return new Vec3(
			tag.getDouble("X"),
			tag.getDouble("Y"),
			tag.getDouble("Z")
		);
	}
}
