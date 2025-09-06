package ru.minced.mixin.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.util.IMinecraft;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen implements IMinecraft {

    @Unique
    List<AbstractDraggable> draggable = Minced.getInstance()
            .getDraggableManager()
            .getDraggable();

    protected ChatScreenMixin() {
        super(Text.empty());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        draggable.stream()
                .filter(draggable -> draggable.visible() && draggable.isDragging())
                .reduce((first, second) -> second)
                .ifPresent(active -> draggable.forEach(draggable -> {
                    if (active == draggable) {
                        draggable.renderWithFixedScale(context, mouseX, mouseY, delta);
                    }
                }));
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        draggable.forEach(draggable -> draggable.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggable.forEach(draggable -> draggable.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }
}