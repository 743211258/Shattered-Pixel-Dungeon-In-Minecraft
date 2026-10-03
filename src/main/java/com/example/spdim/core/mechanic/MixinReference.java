package com.example.spdim.core.mechanic;

import com.example.spdim.core.data_structure.ViscosityRenderData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MixinReference {
	public static final Map<UUID, ViscosityRenderData> renderReference = new HashMap<>();
	public static final Map<UUID, ViscosityRenderData> totalDamageRenderReference = new HashMap<>();
}
