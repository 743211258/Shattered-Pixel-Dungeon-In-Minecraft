package com.example.spdim.core.renderer;

import com.example.spdim.core.Macro;
import com.example.spdim.core.particles.MagicParticle;
import com.example.spdim.core.particles.MagicParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public final class MagicParticleRenderer {

	private static final Random RANDOM = new Random();

	private MagicParticleRenderer() {}

	public static void render(Vec3 origin, Vec3 forward, float[][] colors, float[] weights) {
		Minecraft minecraft = Minecraft.getInstance();

		ClientLevel level = minecraft.level;

		if (level == null) {
			return;
		}

		SpriteSet sprites = MagicParticleProvider.getSprites();

		if (sprites == null) {
			return;
		}

		forward = forward.normalize();

		Vec3 worldUp = Math.abs(forward.y) > 0.99
				? new Vec3(1, 0, 0)
				: new Vec3(0, 1, 0);

		Vec3 right = forward.cross(worldUp).normalize();
		Vec3 up = right.cross(forward).normalize();

		for (double d = 0;
			 d <= Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT;
			 d += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_HEIGHT) {

			double radius = d
					/ Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT
					* Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS;

			double radiusSqr = radius * radius;

			for (double x = -radius;
				 x <= radius;
				 x += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {

				for (double y = -radius;
					 y <= radius;
					 y += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {

					if (x * x + y * y > radiusSqr) {
						continue;
					}

					Vec3 pos = origin
							.add(forward.scale(d))
							.add(right.scale(x))
							.add(up.scale(y));

					float totalWeight = 0.0F;

					for (float weight : weights) {
						totalWeight += weight;
					}

					float randomWeight = RANDOM.nextFloat() * totalWeight;
					int colorIndex = 0;

					for (; colorIndex < weights.length - 1; colorIndex++) {
						randomWeight -= weights[colorIndex];

						if (randomWeight < 0.0F) {
							break;
						}
					}

					float red = colors[colorIndex][0];
					float green = colors[colorIndex][1];
					float blue = colors[colorIndex][2];

					MagicParticle particle = new MagicParticle(
							level,
							pos.x, pos.y, pos.z,
							0.0, 0.0, 0.0,
							red, green, blue
					);

					particle.pickSprite(sprites);
					minecraft.particleEngine.add(particle);
				}
			}
		}
	}
}
