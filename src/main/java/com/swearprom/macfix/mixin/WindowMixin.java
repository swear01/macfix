package com.swearprom.macfix.mixin;

import com.mojang.blaze3d.platform.Window;
import com.swearprom.macfix.FieldEditorStub;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
abstract class WindowMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void macfix$installFieldEditorStub(CallbackInfo ci) {
        if (!Minecraft.ON_OSX) {
            return;
        }
        FieldEditorStub.install((Window) (Object) this);
    }
}
