package ru.minced.mixin.client;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.client.BrandSpoofModule;

@Mixin({ClientBrandRetriever.class})
public class ClientBrandRetrieverMixin {
    @Inject(method = "getClientModName", at = {@At("HEAD")}, cancellable = true, remap = false)
    private static void getClientModNameHook(CallbackInfoReturnable<String> cir) {
        BrandSpoofModule clientSpoof = Minced.getInstance().getModuleManager().getBrandSpoofModule();
        if (clientSpoof.isState()) {
            cir.setReturnValue(BrandSpoofModule.getCustomBrand().getText());
        }
    }
}
