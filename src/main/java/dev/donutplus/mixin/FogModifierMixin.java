package dev.donutplus.mixin;

import dev.donutplus.SkyManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Optional: tints the horizon fog. Silently does nothing if the target changes in a future version. */
@Mixin(targets = "net.minecraft.client.render.fog.AtmosphericFogModifier")
public class FogModifierMixin {
    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void donutplus$fog(ClientWorld world, Camera camera, int viewDistance, float skyDarkness, CallbackInfoReturnable<Integer> cir) {
        Integer c = SkyManager.fog();
        if (c != null) cir.setReturnValue(c);
    }
}
