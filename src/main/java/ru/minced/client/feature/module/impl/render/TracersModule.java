package ru.minced.client.feature.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.TargetSelector;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.util.math.MathUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TracersModule extends Module implements IMinecraft {

    private final ModeListSetting targetTypes = new ModeListSetting("Targets",
            Arrays.stream(TargetSelector.TargetType.values())
                    .map(TargetSelector.TargetType::getDisplayName)
                    .toArray(String[]::new));

    private final TargetSelector targetSelector = new TargetSelector();
    private Color themeColor;

    public TracersModule() {
        super("Tracers", "Draws lines to targets", Category.Visuals);
        addSettings(targetTypes);
    }

    @EventHandler
    public void onRender(EventWorld eventWorld) {
        if (IMinecraft.nullCheck()) return;

        themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

        Set<TargetSelector.TargetType> types = getSelectedTargetTypes();
        if (types.isEmpty()) return;

        targetSelector.searchTargetsInRadius(90.0f);
        targetSelector.getAllTargets(types).forEach(entity ->
                drawTracer(eventWorld.getStack(), entity, getTracerColor(entity))
        );
    }

    private Set<TargetSelector.TargetType> getSelectedTargetTypes() {
        return Arrays.stream(TargetSelector.TargetType.values())
                .filter(type -> targetTypes.isSelected(type.getDisplayName()))
                .collect(Collectors.toSet());
    }

    private Color getTracerColor(Entity entity) {
        if (entity instanceof PlayerEntity && FriendsManager.checkFriend(entity.getName().getString())) {
            return new Color(DisplayModule.getFriendColor().getRGB());
        }

        return new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 200);
    }

    public static void drawTracer(MatrixStack matrices, Entity entity, Color color) {
        if (entity == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        double posX = MathUtil.interpolate(entity.prevX, entity.getX(), MathUtil.getTickDelta()) - cameraPos.x;
        double posY = MathUtil.interpolate(entity.prevY, entity.getY(), MathUtil.getTickDelta()) - cameraPos.y;
        double posZ = MathUtil.interpolate(entity.prevZ, entity.getZ(), MathUtil.getTickDelta()) - cameraPos.z;

        float pitch = camera.getPitch() * (float)(Math.PI / 180.0);
        float yaw = camera.getYaw() * (float)(Math.PI / 180.0);

        float eyeX = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        float eyeY = -MathHelper.sin(pitch);
        float eyeZ = MathHelper.cos(yaw) * MathHelper.cos(pitch);

        float eyeOffset = 0.5f;

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        matrices.push();

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        int colorARGB = color.getRGB();

        buffer.vertex(matrix, eyeX * eyeOffset, eyeY * eyeOffset, eyeZ * eyeOffset).color(colorARGB);
        buffer.vertex(matrix, (float)posX, (float)posY, (float)posZ).color(colorARGB);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        matrices.pop();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}