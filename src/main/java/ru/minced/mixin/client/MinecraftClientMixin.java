package ru.minced.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.core.file.expection.FileProcessingException;
import ru.minced.client.util.font.Fonts;
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyArg(method = "updateWindowTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setTitle(Ljava/lang/String;)V"), index = 0)
    private String modifyWindowTitle(String original) {
        return original.replace("Minecraft", "Minced");
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void onInit(RunArgs args, CallbackInfo ci) {
        try {
            Fonts.initFonts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "stop")
    private void stop(CallbackInfo ci) {
        if (Minced.getInstance() != null) {
            try {
                Minced.getInstance().getFileController().saveFiles();
            } catch (FileProcessingException e) {
                e.printStackTrace();
            } finally {
                Minced.getInstance().getFileController().stopAutoSave();
            }
        }
    }
}
