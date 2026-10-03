package com.example.spdim.core.codec;

import net.minecraft.nbt.CompoundTag;

import com.example.spdim.core.data_structure.ViscosityRenderData;
import com.example.spdim.core.interfaces.CodecToNBT;

public class ViscosityRenderDataCodec implements CodecToNBT<ViscosityRenderData> {

	@Override
	public CompoundTag encode(ViscosityRenderData value) {
		CompoundTag tag = new CompoundTag();

		tag.putFloat("HealthMin", value.healthMin);
		tag.putFloat("HealthMax", value.healthMax);
		tag.putFloat("AbsorptionMin", value.absorptionMin);
		tag.putFloat("AbsorptionMax", value.absorptionMax);

		return tag;
	}

	@Override
	public ViscosityRenderData decode(CompoundTag tag) {
		return new ViscosityRenderData(
			tag.getFloat("HealthMin"),
			tag.getFloat("HealthMax"),
			tag.getFloat("AbsorptionMin"),
			tag.getFloat("AbsorptionMax")
		);
	}
}
