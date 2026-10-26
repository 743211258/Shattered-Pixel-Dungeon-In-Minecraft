package com.example.spdim.core.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class MagicParticle extends TextureSheetParticle {

    public static final float INITIAL_SIZE = 1.0F / 16.0F;
    public static final float FINAL_SIZE = 2.0F / 16.0F;
    public static final int LIFETIME = 30;

    private final float startRed;
    private final float startGreen;
    private final float startBlue;

    public MagicParticle(
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd,
            float red, float green, float blue
    ) {
        super(level, x, y, z, xd, yd, zd);

        this.startRed = red;
        this.startGreen = green;
        this.startBlue = blue;

        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;

        this.quadSize = INITIAL_SIZE;
        this.alpha = 1.0F;
        this.lifetime = LIFETIME;
        this.gravity = 0.0F;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.removed) {
            return;
        }

        float progress = Mth.clamp(
                (float) this.age / this.lifetime,
                0.0F,
                1.0F
        );

        this.quadSize = Mth.lerp(
                progress,
                INITIAL_SIZE,
                FINAL_SIZE
        );

        this.alpha = 1.0F - progress;

        // Keep the initial color; color animation can be added here.
        this.rCol = startRed;
        this.gCol = startGreen;
        this.bCol = startBlue;
    }

    @Override
    public net.minecraft.client.particle.ParticleRenderType getRenderType() {
        return net.minecraft.client.particle.ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
