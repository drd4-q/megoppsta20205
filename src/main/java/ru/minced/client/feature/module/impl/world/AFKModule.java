package ru.minced.client.feature.module.impl.world;

import lombok.Setter;
import lombok.experimental.NonFinal;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

import java.util.Random;

public class AFKModule extends Module {
    private final SliderSetting intervalSetting = new SliderSetting("Interval", 5.0f, 1.0f, 60.0f, 1.0f);
    private final ModeSetting actionSetting = new ModeSetting("Action", "Jump", "Command");
    private final BooleanSetting shiftSetting = new BooleanSetting("Hold shift", false);
    
    @NonFinal
    @Setter
    private int tickCounter = 0;
    private final Random random = new Random();
    
    public AFKModule() {
        super("AFK", "Автоматические действия при AFK", Category.World);
        addSettings(intervalSetting, actionSetting, shiftSetting);
    }
    
    @EventHandler
    public void onTick(EventTick e) {
        if (mc.player == null || mc.world == null) return;

        if (shiftSetting.isState()) {
            mc.player.setSneaking(true);
            mc.options.sneakKey.setPressed(true);
        }

        tickCounter++;
        int intervalTicks = (int) (intervalSetting.getValue() * 20);
        
        if (tickCounter >= intervalTicks) {
            performAction();
            tickCounter = 0;
        }
    }
    
    private void performAction() {
        if (actionSetting.isSelected("Jump")) {
            assert mc.player != null;
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }
        } else if (actionSetting.isSelected("Command")) {
            String randomCommand = generateRandomString(6);
            assert mc.player != null;
            mc.player.networkHandler.sendChatCommand(randomCommand);
        }
    }
    
    private String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
    
    @Override
    public void onDisabled() {
        if (mc.player != null && shiftSetting.isState()) {
            mc.player.setSneaking(false);
            mc.options.sneakKey.setPressed(false);
        }
        super.onDisabled();
    }
} 