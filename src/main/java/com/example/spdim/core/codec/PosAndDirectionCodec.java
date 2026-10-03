package com.example.spdim.core.codec;

import com.example.spdim.core.data_structure.PosAndDirection;
import com.example.spdim.core.interfaces.CodecToNBT;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class PosAndDirectionCodec implements CodecToNBT<PosAndDirection> {

	@Override
	public CompoundTag encode(PosAndDirection value) {
		CompoundTag tag = new CompoundTag();

		tag.putDouble("PosX", value.pos.x);
		tag.putDouble("PosY", value.pos.y);
		tag.putDouble("PosZ", value.pos.z);

		tag.putFloat("YRot", value.yRot);
		tag.putFloat("XRot", value.xRot);
		tag.putFloat("YHeadRot", value.yHeadRot);
		tag.putFloat("YBodyRot", value.yBodyRot);

		return tag;
	}

	@Override
	public PosAndDirection decode(CompoundTag tag) {
		PosAndDirection value = new PosAndDirection();

		value.pos = new Vec3(
			tag.getDouble("PosX"),
			tag.getDouble("PosY"),
			tag.getDouble("PosZ")
		);

		value.yRot = tag.getFloat("YRot");
		value.xRot = tag.getFloat("XRot");
		value.yHeadRot = tag.getFloat("YHeadRot");
		value.yBodyRot = tag.getFloat("YBodyRot");

		return value;
	}
}
