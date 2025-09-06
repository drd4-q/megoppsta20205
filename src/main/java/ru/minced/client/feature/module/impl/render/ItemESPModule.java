package ru.minced.client.feature.module.impl.render;

import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.util.IMinecraft;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.Renderer3D;

import java.awt.*;
import java.util.List;

import static ru.minced.client.util.math.MathUtil.interpolate;

public class ItemESPModule extends Module implements IMinecraft {

    public ModeSetting espMode = new ModeSetting("Mode", "Box");
    BooleanSetting autoTransparency = new BooleanSetting("Auto transparency", false);

    private String previousMode = "Box";
    private static final float TRANSPARENCY_RADIUS = 5.0f;
    private final Color themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

    public ItemESPModule() {
        super("Item ESP", "Displays items through walls", Category.Visuals);
        addSettings(espMode, autoTransparency);
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        if (!espMode.getSelected().equals(previousMode)) {
            previousMode = espMode.getSelected();
        }
    }

    @EventHandler
    public void onRender(EventWorld event) {
        if (espMode.isSelected("Box")) {
            renderBoxMode(event.getStack());
        } else if (espMode.isSelected("Outline")) {
        }
    }

    private void renderBoxMode(MatrixStack stack) {
        if (IMinecraft.nullCheck()) return;

        assert mc.world != null;
        assert mc.player != null;
        List<ItemEntity> items = mc.world.getEntitiesByClass(
            ItemEntity.class,
            mc.player.getBoundingBox().expand(128.0),
            item -> item != null && item.isAlive()
        );
        
        if (items.isEmpty()) return;

        for (ItemEntity item : items) {
            Box itemBox = calculateEntityBox(item);
            
            Color boxColor = getBoxColor();
            Color outlineColor = getOutlineColor();
            
            if (autoTransparency.isState()) {
                renderWithTransparency(item, itemBox, stack, boxColor, outlineColor);
            } else {
                Renderer3D.drawBox(itemBox, stack, boxColor, outlineColor);
            }
        }
    }
    
    private Box calculateEntityBox(ItemEntity item) {
        if (item == null) return null;

        double x = interpolate(item.prevX, item.getX(), MathUtil.getTickDelta());
        double y = interpolate(item.prevY, item.getY(), MathUtil.getTickDelta());
        double z = interpolate(item.prevZ, item.getZ(), MathUtil.getTickDelta());

        return new Box(
            x - item.getWidth() / 2,
            y,
            z - item.getWidth() / 2,
            x + item.getWidth() / 2,
            y + item.getHeight(),
            z + item.getWidth() / 2
        );
    }
    
    private void renderWithTransparency(ItemEntity item, Box itemBox, MatrixStack stack, Color boxColor, Color outlineColor) {
        assert mc.player != null;
        double distance = mc.player.squaredDistanceTo(item);
        double transparencyDistance = TRANSPARENCY_RADIUS * TRANSPARENCY_RADIUS;
        
        if (distance <= transparencyDistance) {
            float alpha = (float) (distance / transparencyDistance);

            Color adjustedBoxColor = new Color(
                boxColor.getRed(), 
                boxColor.getGreen(), 
                boxColor.getBlue(), 
                (int)(boxColor.getAlpha() * alpha)
            );
            
            Color adjustedOutlineColor = new Color(
                outlineColor.getRed(), 
                outlineColor.getGreen(), 
                outlineColor.getBlue(), 
                (int)(outlineColor.getAlpha() * alpha)
            );
            
            Renderer3D.drawBox(itemBox, stack, adjustedBoxColor, adjustedOutlineColor);
        } else {
            Renderer3D.drawBox(itemBox, stack, boxColor, outlineColor);
        }
    }

    private Color getBoxColor() {
        Color color = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 40);
    }

    private Color getOutlineColor() {
        Color color = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
    }
}