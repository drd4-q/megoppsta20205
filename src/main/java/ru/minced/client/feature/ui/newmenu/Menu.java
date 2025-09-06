package ru.minced.client.feature.ui.newmenu;

import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.ui.newmenu.element.CategoryElement;
import ru.minced.client.feature.ui.newmenu.element.InfoElement;
import ru.minced.client.feature.ui.newmenu.element.ModuleElement;
import ru.minced.client.util.other.ScrollHandler;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.ScaleUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import ru.minced.client.util.IMinecraft;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu extends Screen implements IMinecraft {

    private final InfoElement infoElement;
    private final ModuleElement moduleElement;
    private final CategoryElement categoryElement;
    private final Map<Category, List<Module>> modulesByCategory;
    private List<Module> currentModules;

    private final ScrollHandler scrollHandler = new ScrollHandler();

    public Menu() {
        super(Text.of("Menu"));
        this.infoElement = new InfoElement();
        this.moduleElement = new ModuleElement();
        this.categoryElement = new CategoryElement();

        modulesByCategory = new HashMap<>();
        for (Category category : Category.values()) {
            List<Module> modulesInCategory = Minced.getInstance().getModuleManager().modules().stream()
                    .filter(module -> module.getCategory() == category)
                    .toList();
            modulesByCategory.put(category, modulesInCategory);
        }

        updateCurrentModules();
    }

    private void updateCurrentModules() {
        Category selectedCategory = categoryElement.getSelectedCategory();
        currentModules = modulesByCategory.get(selectedCategory);
        scrollHandler.reset();
    }

    @Override
    public void tick() {
        super.tick();
        updateMaxScroll();
    }
    
    private void updateMaxScroll() {
        int padding = 14;
        int maxModulesPerRow = 4;

        int[] columnHeights = new int[maxModulesPerRow];
        int columnIndex = 0;
        
        for (Module module : currentModules) {
            columnHeights[columnIndex] += (int) (moduleElement.getHeight(module) + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }

        int maxColumnHeight = 0;
        for (int height : columnHeights) {
            maxColumnHeight = Math.max(maxColumnHeight, height);
        }

        int contentHeight = maxColumnHeight;
        int viewportHeight = 540 - 32;
        
        float maxScroll = Math.max(0, contentHeight - viewportHeight);
        scrollHandler.setMax(maxScroll);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ScaleUtil.fixScale(context, mouseX, mouseY, (originalScale, scaledMouseX, scaledMouseY) -> {

            int screenWidth = mc.getWindow().getWidth();
            int screenHeight = mc.getWindow().getHeight();

            int x = (screenWidth - 1100) / 2;
            int y = (screenHeight - 540) / 2;

            MatrixStack matrixStack = context.getMatrices();

            // Header
            DrawHelper.drawStyledRectEx(matrixStack, x, y - 78, 1100, 80, new float[] {20, 0, 0, 20}, new Color(29, 31, 44, 204));
            infoElement.renderHeader(matrixStack, x, y - 78, 80);

            // Footer
            DrawHelper.drawStyledRectEx(matrixStack, x, y + 538, 1100, 80, new float[] {0, 20, 20, 0}, new Color(29, 31, 44, 204));
            infoElement.renderFooter(matrixStack, x, y + 538, 80);
            categoryElement.renderFooter(matrixStack, x, y + 538, 80);

            // Background
            DrawHelper.drawRect(matrixStack, x, y, 1100, 540, 0, new Color(17, 19, 24));
            DrawHelper.drawOutline(matrixStack, x - 2, y - 80, 1100 + 4, 700, 20, new Color(52, 51, 64), 3);

            Minced.getInstance().scissorManager.push(x, y, 1100, 540);
            renderFightModules(matrixStack, x + 16, y + 16);
            Minced.getInstance().scissorManager.pop();

            renderDropdowns(matrixStack);
        });
    }

    private void renderDropdowns(MatrixStack matrixStack) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int startX = (mc.getWindow().getWidth() - 1100) / 2 + 16;
        int startY = (mc.getWindow().getHeight() - 540) / 2 + 16;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            moduleElement.renderDropdowns(matrixStack, currentX, currentY, module);

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (handleBindSettingKeyPress(keyCode)) {
            return true;
        }

        if (keyCode == 256 && shouldCloseOnEsc()) {
            startClosing();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean handleBindSettingKeyPress(int keyCode) {
        for (List<Module> modules : modulesByCategory.values()) {
            for (Module module : modules) {
                if (moduleElement.handleKeyPress(module, keyCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleBindSettingMouseClick(int button) {
        for (List<Module> modules : modulesByCategory.values()) {
            for (Module module : modules) {
                if (moduleElement.handleMouseClick(module, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int currentScale = (int) mc.getWindow().getScaleFactor();
        double scaledMouseX = mouseX * currentScale;
        double scaledMouseY = mouseY * currentScale;

        if (handleBindSettingMouseClick(button)) {
            return true;
        }

        boolean dropdownHandled = handleDropdownClicks(scaledMouseX, scaledMouseY, button);
        if (dropdownHandled) return true;

        boolean handled = handleModuleClicks(scaledMouseX, scaledMouseY, button);
        if (handled) return true;

        int screenHeight = mc.getWindow().getHeight();
        int y = (screenHeight - 540) / 2;

        if (scaledMouseY >= y + 538 && scaledMouseY <= y + 538 + 80) {
            if (categoryElement.mouseClicked(scaledMouseX, scaledMouseY)) {
                Category selectedCategory = categoryElement.getSelectedCategory();
                currentModules = modulesByCategory.get(selectedCategory);
                scrollHandler.reset();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleDropdownClicks(double mouseX, double mouseY, int button) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int startX = (mc.getWindow().getWidth() - 1100) / 2 + 16;
        int startY = (mc.getWindow().getHeight() - 540) / 2 + 16;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            if (moduleElement.dropdownMouseClicked(currentX, currentY, module, mouseX, mouseY, button)) {
                return true;
            }

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }

        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int currentScale = (int) mc.getWindow().getScaleFactor();
        double scaledMouseX = mouseX * currentScale;
        double scaledMouseY = mouseY * currentScale;
        
        boolean handled = handleModuleMouseReleased(scaledMouseX, scaledMouseY, button);
        if (handled) return true;
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int currentScale = (int) mc.getWindow().getScaleFactor();
        double scaledMouseX = mouseX * currentScale;
        double scaledMouseY = mouseY * currentScale;
        
        int screenWidth = mc.getWindow().getWidth();
        int screenHeight = mc.getWindow().getHeight();
        int x = (screenWidth - 1100) / 2;
        int y = (screenHeight - 540) / 2;

        if (scaledMouseX >= x && scaledMouseX <= x + 1100 && 
            scaledMouseY >= y && scaledMouseY <= y + 540) {

            scrollHandler.scroll(verticalAmount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int currentScale = (int) mc.getWindow().getScaleFactor();
        double scaledMouseX = mouseX * currentScale;
        double scaledMouseY = mouseY * currentScale;
        
        boolean handled = handleModuleMouseDragged(scaledMouseX, scaledMouseY, button);
        if (handled) return true;
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void renderFightModules(MatrixStack matrixStack, int startX, int startY) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            int screenHeight = mc.getWindow().getHeight();
            int viewportY = (screenHeight - 540) / 2;
            int viewportHeight = 540;
            
            if (currentY + moduleHeight >= viewportY - moduleHeight && currentY <= viewportY + viewportHeight) {
                moduleElement.render(matrixStack, currentX, currentY, module);
            }

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }
    }

    private boolean handleModuleClicks(double mouseX, double mouseY, int button) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int startX = (mc.getWindow().getWidth() - 1100) / 2 + 16;
        int startY = (mc.getWindow().getHeight() - 540) / 2 + 16;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            if (moduleElement.mouseClicked(currentX, currentY, module, mouseX, mouseY, button)) {
                return true;
            }

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }

        return false;
    }
    
    private boolean handleModuleMouseReleased(double mouseX, double mouseY, int button) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int startX = (mc.getWindow().getWidth() - 1100) / 2 + 16;
        int startY = (mc.getWindow().getHeight() - 540) / 2 + 16;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            if (moduleElement.mouseReleased(currentX, currentY, module, mouseX, mouseY, button)) {
                return true;
            }

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }

        return false;
    }
    
    private boolean handleModuleMouseDragged(double mouseX, double mouseY, int button) {
        int padding = 14;
        int moduleWidth = 255;
        int maxModulesPerRow = 4;

        int startX = (mc.getWindow().getWidth() - 1100) / 2 + 16;
        int startY = (mc.getWindow().getHeight() - 540) / 2 + 16;

        int[] columnPositions = new int[maxModulesPerRow];
        for (int i = 0; i < maxModulesPerRow; i++) {
            columnPositions[i] = startX + i * (moduleWidth + padding);
        }

        int[] columnYPositions = new int[maxModulesPerRow];
        Arrays.fill(columnYPositions, startY);

        int columnIndex = 0;

        for (Module module : currentModules) {
            float moduleHeight = moduleElement.getHeight(module);
            int currentX = columnPositions[columnIndex];
            int currentY = columnYPositions[columnIndex] + (int) scrollHandler.getValue();

            if (moduleElement.mouseDragged(currentX, currentY, module, mouseX, mouseY, button)) {
                return true;
            }

            columnYPositions[columnIndex] += (int) (moduleHeight + padding);
            columnIndex = (columnIndex + 1) % maxModulesPerRow;
        }

        return false;
    }

    private void startClosing() {
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
    }
}
