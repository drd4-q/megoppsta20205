package ru.minced.client.feature.ui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import ru.minced.client.feature.ui.menu.components.CategoryPanel;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.impl.FadeAnimation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static ru.minced.client.util.other.animation.Direction.BACKWARDS;
import static ru.minced.client.util.other.animation.Direction.FORWARDS;


public class GuiScreen extends Screen implements IMinecraft {

    private int height;
    private float scrollOffset = 0;
    private final ArrayList<CategoryPanel> categoryPanels = new ArrayList<>();

    private final Animation fadeAnimation = new FadeAnimation()
            .setMs(100)
            .setValue(1);
            
    private boolean closing = false;

    public GuiScreen() {
        super(Text.of("Minced"));
        fadeAnimation.setDirection(FORWARDS);

        for (Category category : Category.values()) {
            categoryPanels.add(new CategoryPanel(category, 0, 0));
        }
    }

    @Override
    public void tick() {
        close();
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        
        height = 15;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        float xWindow = screenWidth / 2.0f;
        float yWindow = screenHeight / 2.0f;

        float totalWidth = categoryPanels.size() * 90;
        float x = xWindow - totalWidth / 2;
        float y = yWindow - 190;

        float currentX = x;
        for (CategoryPanel panel : categoryPanels) {
            panel.setX(currentX);
            panel.setY(y + scrollOffset);
            currentX += 90;
        }

        fadeAnimation.update();
        float opacity = fadeAnimation.getOutput().floatValue();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

        categoryPanels.forEach(p -> {
            height = 0;
            ModuleManager.modules.stream().filter(m -> m.getCategory() == p.getCategory()).forEach(m -> height += 15);
            p.render(context, height, mouseX, mouseY, delta);
        });

        windowManager.render(context, mouseX, mouseY, delta);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        windowManager.mouseClicked(mouseX, mouseY, button);
        categoryPanels.forEach(p -> {
            p.mouseClicked(mouseX, mouseY, button);
        });

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        windowManager.mouseReleased(mouseX, mouseY, button);
        categoryPanels.forEach(p -> {
            p.mouseReleased(mouseX, mouseY, button);
        });

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        windowManager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && shouldCloseOnEsc()) {
            startClosing();
            return true;
        }

        categoryPanels.forEach(p -> {
            p.keyPressed(keyCode, scanCode, modifiers);
        });

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void startClosing() {
        if (!closing) {
            closing = true;
            fadeAnimation.setDirection(BACKWARDS);

            categoryPanels.forEach(panel -> panel.setClosing(true));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset += (float) (verticalAmount * 10);

        float maxScroll = 500;
        scrollOffset = Math.max(-maxScroll, Math.min(maxScroll, scrollOffset));

        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (closing && fadeAnimation.isFinished(BACKWARDS)) {
            boolean allPanelsClosed = true;
            for (CategoryPanel panel : categoryPanels) {
                if (!panel.isCloseAnimationFinished()) {
                    allPanelsClosed = false;
                    break;
                }
            }

            if (allPanelsClosed) {
                windowManager.getWindows().forEach(windowManager::delete);
                super.close();
            }
        }
    }
}