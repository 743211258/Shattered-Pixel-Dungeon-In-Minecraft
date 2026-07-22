package com.example.spdim.core.data_structure;

import java.util.Objects;

public class ViscosityRender {
    public float healthMin;
    public float healthMax;
    public float absorptionMin;
    public float absorptionMax;

    public ViscosityRender(float healthMin, float healthMax, float absorptionMin, float absorptionMax) {
        this.healthMin = healthMin;
        this.healthMax = healthMax;
        this.absorptionMin = absorptionMin;
        this.absorptionMax = absorptionMax;
    }
}

