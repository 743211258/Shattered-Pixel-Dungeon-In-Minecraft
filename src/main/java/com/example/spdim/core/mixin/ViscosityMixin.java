package com.example.spdim.core.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.example.spdim.core.data_structure.ViscosityRender;
import com.example.spdim.core.mechanic.MixinReference;

import java.util.UUID;
@Mixin(Gui.class)
public class ViscosityMixin {
	public ResourceLocation TEXTURE = new ResourceLocation("spdim", "textures/item/a_bubble.png");
	@WrapOperation(
		method = "renderHearts",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V"
		)//,
		//locals = LocalCapture.CAPTURE_FAILHARD
	)
	/*private void beforeRenderHeart(
		GuiGraphics graphics,
		Player player,
		int x,
		int y,
		int lineHeight,
		int highlightHeart,
		float health,
		int displayHealth,
		int lastHealth,
		int absorption,
		boolean blinking,
		CallbackInfo ci,

		@Local(ordinal = 11) int i1,
		@Local(ordinal = 14) int l1,
		@Local(ordinal = 15) int i2
	) {
		if (i1 < 4) {
			graphics.blit(
				TEXTURE,
				l1,
				i2,
				0,
				0,
				8,
				8,
				8,
				8
			);
			return;
		}
	}*/

	private void beforeRenderHeart(
		Gui instance,
		GuiGraphics graphics,
		@Coerce Object heartType,
		int x,
		int y,
		int height,
		boolean blinking,
		boolean half,
		Operation<Void> original,

		@Local(ordinal = 11) int i1,
    @Local(ordinal = 14) int l1,
    @Local(ordinal = 15) int i2
	) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			original.call(
				instance,
				graphics,
				heartType,
				x,
				y,
				height,
				blinking,
				half
			);
			return;	
		}
		UUID uuid = player.getUUID();
		ViscosityRender reference = MixinReference.renderReference.get(uuid);
		if (reference == null) {
			original.call(
				instance,
				graphics,
				heartType,
				x,
				y,
				height,
				blinking,
				half
			);	
			return;
		}
       /*if (i1 == 0) {
            System.out.println(String.format(
                "[Viscosity Render] hMin: %.2f | hMax: %.2f | aMin: %.2f | aMax: %.2f",
                reference.healthMin, reference.healthMax, reference.absorptionMin, reference.absorptionMax
            ));
        }*/
		float currentPosition = (float) i1 + 1.0F;
		if ((currentPosition >= reference.healthMin && currentPosition <= reference.healthMax) || (currentPosition >= reference.absorptionMin && currentPosition <= reference.absorptionMax)) {
			graphics.blit(
				TEXTURE,
				x,
				y,
				0,
				0,
				8,
				8,
				8,
				8
			);
		} else {
			original.call(
				instance,
				graphics,
				heartType,
				x,
				y,
				height,
				blinking,
				half
			);
		}
	}
}
