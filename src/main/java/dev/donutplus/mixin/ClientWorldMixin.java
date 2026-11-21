package dev.donutplus.mixin;

import dev.donutplus.SkyManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void donutplus$sky(Vec3d cameraPos, float tickProgress, CallbackInfoReturnable<Integer> cir) {
        Integer c = SkyManager.sky();
        if (c != null) cir.setReturnValue(c);
    }

    @Inject(method = "getCloudsColor", at = @At("RETURN"), cancellable = true)
    private void donutplus$clouds(float tickProgress, CallbackInfoReturnable<Integer> cir) {
        Integer c = SkyManager.clouds();
        if (c != null) cir.setReturnValue(c);
    }
}
