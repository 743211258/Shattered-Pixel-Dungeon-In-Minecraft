package com.example.spdim.core.renderer;

import com.example.spdim.core.particles.MagicParticle;
import com.example.spdim.core.particles.MagicParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class MagicParticleRenderer {

	private MagicParticleRenderer() {}

	public static void render(List<Vec3> pos_list, List<float[]> color_list) {
		if (pos_list.size() != color_list.size()) {
			throw new IllegalArgumentException("Position and color list sizes must match.");
		}

		Minecraft minecraft = Minecraft.getInstance();

		ClientLevel level = minecraft.level;

		if (level == null) {
			return;
		}

		SpriteSet sprites = MagicParticleProvider.getSprites();

		if (sprites == null) {
			return;
		}

		for (int i = 0; i < pos_list.size(); i++) {
			Vec3 pos = pos_list.get(i);
			float[] color = color_list.get(i);

			MagicParticle particle = new MagicParticle(
					level,
					pos.x, pos.y, pos.z,
					0.0, 0.0, 0.0,
					color[0], color[1], color[2]
			);

			particle.pickSprite(sprites);
			minecraft.particleEngine.add(particle);
		}
	}
}

