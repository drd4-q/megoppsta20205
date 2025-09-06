package ru.minced.mixin.misc;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.player.NameProtectModule;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(value = {TextVisitFactory.class})
public class TextVisitFactoryMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z" }, index = 0)
    private static String adjustText(String text) {
        return protect(text);
    }

    @Unique
    private static String protect(String string) {
        if (!Minced.getInstance().getModuleManager().getNameProtectModule().isState() || mc.player == null)
            return string;
        String me = mc.player.getName().getString();
        if (string.contains(me))
            return string.replace(me, NameProtectModule.getCustomName().getText());

        return string;
    }
}