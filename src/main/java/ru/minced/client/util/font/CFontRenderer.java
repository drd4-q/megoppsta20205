package ru.minced.client.util.font;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.font.glyph.Glyph;
import ru.minced.client.util.font.glyph.GlyphMap;

import java.awt.*;
import java.io.Closeable;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR;

public class CFontRenderer implements Closeable, IMinecraft {
    private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap() {{
        put('0', 0x000000);
        put('1', 0x0000AA);
        put('2', 0x00AA00);
        put('3', 0x00AAAA);
        put('4', 0xAA0000);
        put('5', 0xAA00AA);
        put('6', 0xFFAA00);
        put('7', 0xAAAAAA);
        put('8', 0x555555);
        put('9', 0x5555FF);
        put('A', 0x55FF55);
        put('B', 0x55FFFF);
        put('C', 0xFF5555);
        put('D', 0xFF55FF);
        put('E', 0xFFFF55);
        put('F', 0xFFFFFF);
    }};

    private static final ExecutorService ASYNC_WORKER = Executors.newCachedThreadPool();
    private final Object2ObjectMap<Identifier, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectOpenHashMap<>();
    private final float originalSize;
    private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
    private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap<>();
    private final int charsPerPage;
    private final int padding;
    private final String prebakeGlyphs;
    private int scaleMul = 0;
    private Font font;
    private int previousGameScale = -1;
    private Future<Void> prebakeGlyphsFuture;
    private boolean initialized;

    public CFontRenderer(Font font, float sizePx, int charactersPerPage, int paddingBetweenCharacters, @Nullable String prebakeCharacters) {
        this.originalSize = sizePx;
        this.charsPerPage = charactersPerPage;
        this.padding = paddingBetweenCharacters;
        this.prebakeGlyphs = prebakeCharacters;
        init(font, sizePx);
    }

    public CFontRenderer(Font font, float sizePx) {
        this(font, sizePx, 256, 5, null);
    }

    private static int floorNearestMulN(int x, int n) {
        return n * (int) Math.floor((double) x / (double) n);
    }

    public static String stripControlCodes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder f = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '§') {
                i++;
                continue;
            }
            f.append(c);
        }
        return f.toString();
    }

    private void sizeCheck() {
        int gs = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        if (gs != this.previousGameScale) {
            close();
            init(this.font, this.originalSize);
        }
    }

    private void init(Font font, float sizePx) {
        if (initialized) throw new IllegalStateException("Double call to init()");
        initialized = true;
        this.previousGameScale = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        this.scaleMul = this.previousGameScale;
        this.font = font.deriveFont(sizePx * this.scaleMul);
        if (prebakeGlyphs != null && !prebakeGlyphs.isEmpty()) {
            prebakeGlyphsFuture = this.prebake();
        }
    }

    private Future<Void> prebake() {
        return ASYNC_WORKER.submit(() -> {
            for (char c : prebakeGlyphs.toCharArray()) {
                if (Thread.interrupted()) break;
                locateGlyph1(c);
            }
            return null;
        });
    }

    private GlyphMap generateMap(char from, char to) {
        GlyphMap gm = new GlyphMap(from, to, this.font, randomIdentifier(), padding);
        maps.add(gm);
        return gm;
    }

    private Glyph locateGlyph0(char glyph) {
        for (GlyphMap map : maps) {
            if (map.contains(glyph)) {
                return map.getGlyph(glyph);
            }
        }
        int base = floorNearestMulN(glyph, charsPerPage);
        GlyphMap glyphMap = generateMap((char) base, (char) (base + charsPerPage));
        return glyphMap.getGlyph(glyph);
    }

    private Glyph locateGlyph1(char glyph) {
        return allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
    }

    public void drawGradientString(MatrixStack stack, String text, double x, double y, Color colorStart, Color colorEnd) {
        float scale = (float) net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaleFactor();
        float defaultScale = 2.0f;
        float scaleFactor = defaultScale / scale;
        
        stack.push();
        float offsetY = 3f * scaleFactor; 
        y -= offsetY;

        stack.translate(x, y, 0);
        stack.scale(0.5f * scaleFactor, 0.5f * scaleFactor, 0.5f * scaleFactor);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        char[] chars = text.toCharArray();

        float xOffset = 0;
        float yOffset = 0;
        int lineStart = 0;
        int textLength = text.length();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n') {
                yOffset += getStringHeight(text.substring(lineStart, i)) - 2;
                xOffset = 0;
                lineStart = i + 1;
                continue;
            }
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                if (glyph.value() != ' ') {
                    float t = (float) i / (textLength - 1);
                    int color = interpolateColor(colorStart.getRGB(), colorEnd.getRGB(), t);
                    DrawEntry entry = new DrawEntry(xOffset, yOffset, color, glyph);
                    Identifier textureId = glyph.owner().bindToTexture;

                    GLYPH_PAGE_CACHE.computeIfAbsent(textureId, integer -> new ObjectArrayList<>()).add(entry);
                }
                xOffset += glyph.width();
            }
        }

        drawGlyphs(matrix4f);

        GLYPH_PAGE_CACHE.clear();
        stack.pop();
    }

    public void drawString(MatrixStack stack, String text, float x, float y, int color) {
        float scale = (float) net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaleFactor();
        float defaultScale = 2.0f;
        float scaleFactor = defaultScale / scale;
        
        stack.push();
        float offsetY = 3f * scaleFactor;
        y -= offsetY;

        stack.translate(x, y, 0);
        stack.scale(0.5f * scaleFactor, 0.5f * scaleFactor, 0.5f * scaleFactor);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        Matrix4f matrix4f = stack
                .peek()
                .getPositionMatrix();

        char[] chars = text.toCharArray();

        float xOffset = 0;
        float yOffset = 0;
        int lineStart = 0;


        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n') {
                yOffset += getStringHeight(text.substring(lineStart, i)) - 2;
                xOffset = 0;
                lineStart = i + 1;
                continue;
            }
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                if (glyph.value() != ' ') {
                    Identifier i1 = glyph.owner().bindToTexture;
                    DrawEntry entry = new DrawEntry(xOffset, yOffset, color, glyph);
                    GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
                }
                xOffset += glyph.width();
            }
        }

        drawGlyphs(matrix4f);

        GLYPH_PAGE_CACHE.clear();
        stack.pop();
    }

    public float getStringWidth(String text) {
        float scale = (float) net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaleFactor();
        float defaultScale = 2.0f;
        float scaleFactor = defaultScale / scale;
        
        char[] c = stripControlCodes(text).toCharArray();
        float currentLine = 0;
        float maxPreviousLines = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine +=  glyph == null ? 0 : (glyph.width() / (float) this.scaleMul);
        }
        return Math.max(currentLine, maxPreviousLines) * scaleFactor;
    }

    public float getStringHeight(String text) {
        float scale = (float) net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaleFactor();
        float defaultScale = 2.0f;
        float scaleFactor = defaultScale / scale;
        
        char[] c = stripControlCodes(text).toCharArray();
        if (c.length == 0) {
            c = new char[]{' '};
        }
        float currentLine = 0;
        float previous = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                if (currentLine == 0) {
                    currentLine = (locateGlyph1(' ') == null ? 0 : (Objects.requireNonNull(locateGlyph1(' ')).height() / (float) this.scaleMul));
                }
                previous += currentLine;
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine = Math.max(glyph == null ? 0 : (glyph.height() / (float) this.scaleMul), currentLine);
        }
        return (currentLine + previous) * scaleFactor;
    }


    @Override
    public void close() {
        try {
            if (prebakeGlyphsFuture != null && !prebakeGlyphsFuture.isDone() && !prebakeGlyphsFuture.isCancelled()) {
                prebakeGlyphsFuture.cancel(true);
                prebakeGlyphsFuture.get();
                prebakeGlyphsFuture = null;
            }
            for (GlyphMap map : maps) {
                map.destroy();
            }
            maps.clear();
            allGlyphs.clear();
            initialized = false;
        } catch (Exception ignored) {
        }
    }

    @Contract(value = "-> new", pure = true)
    public static @NotNull Identifier randomIdentifier() {
        return Identifier.of("wuq", "temp/" + randomString(32));
    }

    private static String randomString(int length) {
        return IntStream.range(0, length)
                .mapToObj(operand -> String.valueOf((char) new Random().nextInt('a', 'z' + 1)))
                .collect(Collectors.joining());
    }

    @Contract(value = "_ -> new", pure = true)
    public static int @NotNull [] RGBIntToRGB(int in) {
        int red = in >> 8 * 2 & 0xFF;
        int green = in >> 8 & 0xFF;
        int blue = in & 0xFF;
        return new int[]{red, green, blue};
    }

    private Glyph locateGlyph(char glyph) {
        for (GlyphMap map : maps) {
            if (map.contains(glyph)) {
                return map.getGlyph(glyph);
            }
        }

        char base = (char) floorNearestMulN(glyph, 256);
        return generateMap(base, (char) (base + 256))
                .getGlyph(glyph);
    }

    private int interpolateColor(int colorStart, int colorEnd, float t) {
        float startAlpha = (colorStart >> 24 & 255) / 255.0F;
        float startRed = (colorStart >> 16 & 255) / 255.0F;
        float startGreen = (colorStart >> 8 & 255) / 255.0F;
        float startBlue = (colorStart & 255) / 255.0F;

        float endAlpha = (colorEnd >> 24 & 255) / 255.0F;
        float endRed = (colorEnd >> 16 & 255) / 255.0F;
        float endGreen = (colorEnd >> 8 & 255) / 255.0F;
        float endBlue = (colorEnd & 255) / 255.0F;

        float alpha = startAlpha + t * (endAlpha - startAlpha);
        float red = startRed + t * (endRed - startRed);
        float green = startGreen + t * (endGreen - startGreen);
        float blue = startBlue + t * (endBlue - startBlue);

        return ((int) (alpha * 255.0F) << 24) | ((int) (red * 255.0F) << 16) | ((int) (green * 255.0F) << 8) | (int) (blue * 255.0F);
    }

    private void drawGlyphs(Matrix4f matrix) {
        for (Identifier identifier : GLYPH_PAGE_CACHE.keySet()) {
            RenderSystem.setShaderTexture(0, identifier);
            BufferBuilder builder = Tessellator.getInstance().begin(QUADS, POSITION_TEXTURE_COLOR);
            {
                for (DrawEntry drawEntry : GLYPH_PAGE_CACHE.get(identifier)) {
                    float x = drawEntry.atX();
                    float y = drawEntry.atY();

                    Glyph glyph = drawEntry.toDraw();
                    GlyphMap glyphMap = glyph.owner();

                    float width = glyph.width();
                    float height = glyph.height();

                    float u1 = (float) glyph.u() / glyphMap.width;
                    float v1 = (float) glyph.v() / glyphMap.height;
                    float u2 = (float) (glyph.u() + glyph.width()) / glyphMap.width;
                    float v2 = (float) (glyph.v() + glyph.height()) / glyphMap.height;

                    int color = drawEntry.color();

                    builder.vertex(matrix, x + 0, y + height, 0).texture(u1, v2).color(color);
                    builder.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(color);
                    builder.vertex(matrix, x + width, y + 0, 0).texture(u2, v1).color(color);
                    builder.vertex(matrix, x + 0, y + 0, 0).texture(u1, v1).color(color);
                }
            }
            BufferRenderer.drawWithGlobalProgram(builder.end());
        }
    }

    public float getFontHeight(String str) {
        return getStringHeight(str);
    }

    public record DrawEntry(float atX, float atY, int color, Glyph toDraw) {
    }
}