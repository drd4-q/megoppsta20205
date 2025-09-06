package ru.minced.client.feature.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.util.player.MovingUtil;
import ru.minced.client.util.render.DrawHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.animation.Animation;
import ru.minced.client.util.animation.util.Easings;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.rotation.TargetSelector;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ArrowsModule extends Module {

    private final SliderSetting arrowRadius = new SliderSetting("Radius", 50.0f, 30.0f, 150.0f, 1.0f);
    
    private final ModeListSetting targetTypes = new ModeListSetting("Targets",
            Arrays.stream(TargetSelector.TargetType.values())
                  .map(TargetSelector.TargetType::getDisplayName)
                  .toArray(String[]::new));
    
    private final TargetSelector targetSelector = new TargetSelector();

    public ArrowsModule(){
        super("Arrows", "Draws arrows pointing to targets", Category.Visuals);
        
        addSettings(arrowRadius, targetTypes);
        
        targetTypes.select("Armored Players");
        targetTypes.select("Unarmored Players");
    }

    private final Animation moveAnimation = new Animation();
    private float lastCalculatedRadius = 0f;

    @EventHandler
    public void onUpdate(EventTick e) {
        if (mc.world != null && mc.player != null) {
            targetSelector.searchTargetsInRadius(128f);
            
            float targetRadius = calculateMoveAnimation();

            if (Math.abs(targetRadius - lastCalculatedRadius) > 0.1f) {
                moveAnimation.run(targetRadius, 0.8, Easings.EXPO_OUT);
                lastCalculatedRadius = targetRadius;
            }
        }
    }

    @EventHandler
    public void onRender2D(EventRender eventRender) {
        if (IMinecraft.nullCheck()) return;
        
        Set<TargetSelector.TargetType> types = getSelectedTargetTypes();
        if (types.isEmpty()) return;
        
        moveAnimation.update();
        
        float xOffset = mc.getWindow().getScaledWidth() / 2f;
        float yOffset = mc.getWindow().getScaledHeight() / 2f;
        float radius = moveAnimation.get();
        
        for (LivingEntity entity : targetSelector.getAllTargets(types)) {
            if (entity == mc.player) continue;

            assert mc.player != null;
            float yaw = getRotations(entity, MathUtil.getTickDelta()) - mc.player.getYaw();
            
            eventRender.getContext().getMatrices().push();
            eventRender.getContext().getMatrices().translate(xOffset, yOffset, 0.0F);
            eventRender.getContext().getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
            
            Color color = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());
            if (entity instanceof PlayerEntity player && FriendsManager.checkFriend(player.getName().getString())) {
                color = new Color(DisplayModule.getFriendColor().getRGB());
            }
            DrawHelper.drawImage(eventRender.getStack(), -7.5f, -radius - 7.5f, 15, 15, 0, IMinecraft.arrowPng, color);
            eventRender.getContext().getMatrices().pop();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
    }

    private Set<TargetSelector.TargetType> getSelectedTargetTypes() {
        return Arrays.stream(TargetSelector.TargetType.values())
            .filter(type -> targetTypes.isSelected(type.getDisplayName()))
            .collect(Collectors.toSet());
    }

    public static float getRotations(Entity entity, float tickDelta) {
        if (mc.player == null) return 0;

        double x = MathUtil.interpolate(entity.prevX, entity.getX(), tickDelta)
                - MathUtil.interpolate(mc.player.prevX, mc.player.getX(), tickDelta);
        double z = MathUtil.interpolate(entity.prevZ, entity.getZ(), tickDelta)
                - MathUtil.interpolate(mc.player.prevZ, mc.player.getZ(), tickDelta);

        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }


    private float calculateMoveAnimation() {
        float set;

        if (mc.currentScreen instanceof HandledScreen) {
            set = 160.0f;
        } else {
            set = arrowRadius.getValue();

            assert mc.player != null;
            if (MovingUtil.isMoving()) {
                set += mc.player.isSneaking() ? 5 : 15;
            } else {
                if (mc.player.isSneaking()) {
                    set -= 10;
                }
            }
        }
        
        return set;
    }
}