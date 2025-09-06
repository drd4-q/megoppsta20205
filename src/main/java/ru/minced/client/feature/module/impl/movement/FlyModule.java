package ru.minced.client.feature.module.impl.movement;

import net.minecraft.util.math.MathHelper;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;

public class FlyModule extends Module {

    public FlyModule(){
        super("Fly", "fsdf", Category.Movement);
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        if (mc.player == null || mc.world == null) return;

        double speed = 0.4;

        double motionY = 0;
        if (mc.options.jumpKey.isPressed()) {
            motionY += speed;
        }
        if (mc.options.sneakKey.isPressed()) {
            motionY -= speed;
        }

        double motionX = 0;
        double motionZ = 0;

        if (mc.options.forwardKey.isPressed()) {
            motionX += -MathHelper.sin(mc.player.getYaw() * (float) Math.PI / 180F) * speed;
            motionZ +=  MathHelper.cos(mc.player.getYaw() * (float) Math.PI / 180F) * speed;
        }
        if (mc.options.backKey.isPressed()) {
            motionX +=  MathHelper.sin(mc.player.getYaw() * (float) Math.PI / 180F) * speed;
            motionZ += -MathHelper.cos(mc.player.getYaw() * (float) Math.PI / 180F) * speed;
        }

        if (!mc.options.forwardKey.isPressed() && !mc.options.backKey.isPressed()) {
            motionX = 0;
            motionZ = 0;
        }

        mc.player.setVelocity(motionX, motionY, motionZ);
    }
}