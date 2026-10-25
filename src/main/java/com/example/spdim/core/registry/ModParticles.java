package com.example.spdim.core.registry;

import com.example.spdim.core.particles.MagicParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(
                    ForgeRegistries.PARTICLE_TYPES,
                    "spdim"
            );

    public static final RegistryObject<SimpleParticleType> MAGIC =
            PARTICLES.register(
                    "magic",
                    () -> new SimpleParticleType(false)
            );

    private ModParticles() {}

    @Mod.EventBusSubscriber(
            modid = "spdim",
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static final class ClientRegistration {

        @SubscribeEvent
        public static void registerParticleProviders(
                RegisterParticleProvidersEvent event
        ) {
            event.registerSpriteSet(
                    MAGIC.get(),
                    MagicParticleProvider::new
            );
        }
    }
}
