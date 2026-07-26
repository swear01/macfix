package com.swearprom.macfix.mixin;

import com.swearprom.macfix.FieldEditorStub;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Inject(method = "stop", at = @At("HEAD"))
    private void macfix$ensureStubBeforeStop(CallbackInfo ci) {
        Minecraft self = (Minecraft) (Object) this;
        if (!Minecraft.ON_OSX || self.getWindow() == null) {
            return;
        }
        FieldEditorStub.install(self.getWindow());
    }
}
