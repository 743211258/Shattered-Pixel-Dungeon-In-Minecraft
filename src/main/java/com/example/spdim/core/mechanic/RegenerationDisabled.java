package com.example.spdim.core.mechanic;

import com.example.spdim.core.registry.ModEffects;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RegenerationDisabled extends MobEffect {

  public RegenerationDisabled() {
    super(MobEffectCategory.HARMFUL, 0); 
  }

  public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
    // No implementation
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return true;
  }
  
  @Override
  public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributes, int amplifier) {
    // No implementation
  }

  public static boolean isDisabled(LivingEntity entity) {
    return entity.hasEffect(ModEffects.REGEN_DISABLED.get());
  }
}

