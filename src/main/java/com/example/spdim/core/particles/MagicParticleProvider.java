package com.example.spdim.core.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public final class MagicParticleProvider
        implements ParticleProvider<SimpleParticleType> {

    private static SpriteSet registeredSprites;

    private final SpriteSet sprites;

    public MagicParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
        registeredSprites = sprites;
    }

    public static SpriteSet getSprites() {
        return registeredSprites;
    }

    @Override
    public MagicParticle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd
    ) {
        MagicParticle particle = new MagicParticle(
                level,
                x, y, z,
                xd, yd, zd,
                1.0F, 1.0F, 1.0F
        );

        particle.pickSprite(sprites);
        return particle;
    }
}
