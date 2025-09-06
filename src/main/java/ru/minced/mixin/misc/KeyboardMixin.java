package ru.minced.mixin.misc;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.feature.module.impl.client.ClickGuiModule;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.feature.ui.menu.GuiScreen;
import ru.minced.client.feature.ui.newmenu.Menu;
import ru.minced.client.util.IMinecraft;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            if (MinecraftClient.getInstance().currentScreen == null) {
                if (action == 0) {
                    for (Module module : ModuleManager.modules) {
                        if (key == module.getKey()) {
                            module.toggle();
                        }
                    }
                }

                ClickGuiModule clickGuiModule = Minced.getInstance().getModuleManager().getClickGuiModule();
                if (clickGuiModule != null && key == GLFW.GLFW_KEY_DELETE){
                    mc.setScreen(new GuiScreen());
                }

                if (key == GLFW.GLFW_KEY_RIGHT_SHIFT && action == GLFW.GLFW_PRESS) {
                    mc.setScreen(new Menu());
                }

                if (!IMinecraft.nullCheck()) {
                    EventKey eventKey = new EventKey(action, key);
                    EventManager.post(eventKey);
                }
            }
        }
    }
}