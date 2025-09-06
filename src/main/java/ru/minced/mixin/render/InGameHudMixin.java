package ru.minced.mixin.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.impl.render.NoRenderModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.ScaleUtil;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(at = @At(value = "TAIL"), method = "render")
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (mc.player != null && mc.world != null && !mc.options.hudHidden && !mc.getDebugHud().shouldShowDebugHud()) {
            EventManager.post(new EventRender(context.getMatrices(), context));
            Minced.getInstance()
                    .getDraggableManager()
                    .getDraggable()
                    .forEach(draggable -> {
                        draggable.tick(MathUtil.getTickDelta());

                        if (draggable.visible()) {
                            draggable.visibleDrag();
                        } else {
                            draggable.hideDrag();
                        }

                        float animValue = draggable.animValue();
                        if (animValue > 0) {
                            ScaleUtil.fixScale(context, scale -> {
                                draggable.drawDraggable(context);
                            });
                        }
                    });
        }
    }
    @Inject(at = @At("HEAD"), method = "renderVignetteOverlay", cancellable = true)
    private void onRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
        if (!IMinecraft.nullCheck()) {
            NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
            if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Vignette")) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderScoreboardSidebar*", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, RenderTickCounter counter, CallbackInfo ci) {
        if (!IMinecraft.nullCheck()) {
            NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
            if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Scoreboard")) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!IMinecraft.nullCheck()) {
            DisplayModule displayModule = Minced.getInstance().getModuleManager().getDisplayModule();
            if (displayModule != null && displayModule.isState() && DisplayModule.elements.isSelected("Potions")) {
                ci.cancel();
            }
        }
    }
}