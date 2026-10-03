package com.example.spdim.core.data_structure;

import java.util.Objects;

public class ViscosityRenderData {
	public float healthMin;
	public float healthMax;
	public float absorptionMin;
	public float absorptionMax;

	public ViscosityRenderData(float healthMin, float healthMax, float absorptionMin, float absorptionMax) {
		this.healthMin = healthMin;
		this.healthMax = healthMax;
		this.absorptionMin = absorptionMin;
		this.absorptionMax = absorptionMax;
	}
}

