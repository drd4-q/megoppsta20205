package ru.minced.client.util.other.animation;

import net.minecraft.client.MinecraftClient;

import java.awt.*;

public class AnimationHelper {
    long mc;
    public float anim, anim2, to, speed;
    public AnimationHelper(float anim, float to, float speed){
        this.anim = anim;
        this.to = to;
        this.speed = speed;
        mc = System.currentTimeMillis();
    }

    public float getAnim() {
        int count = (int) ((System.currentTimeMillis() - mc) / 5);
        if (count > 0){
            mc = System.currentTimeMillis();
        }
        for (int i = 0; i < count; i++) {
            anim = lerp(anim, to, speed);
        }
        return anim;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - clamp(deltaTime() * multiple, 0, 1)) * end + clamp(deltaTime() * multiple, 0, 1) * start;
    }

    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public void reset(){
        mc = System.currentTimeMillis();
    }

    public void setAnim(float anim) {
        this.anim = anim;
        this.anim2 = anim;
        mc = System.currentTimeMillis();
    }

    public static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }


    public static float deltaTime() {
        return (float) (MinecraftClient.getInstance().getCurrentFps() > 0 ? 1.0 / (double) MinecraftClient.getInstance().getCurrentFps() : 1.0);
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int applyOpacity(int color_int, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        Color color = new Color(color_int);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity)).getRGB();
    }

    public static float easeOutBounce(float t) {
        if (t < (1 / 2.75f)) {
            return 7.5625f * t * t;
        } else if (t < (2 / 2.75f)) {
            t -= (1.5f / 2.75f);
            return 7.5625f * t * t + 0.75f;
        } else if (t < (2.5 / 2.75)) {
            t -= (2.25f / 2.75f);
            return 7.5625f * t * t + 0.9375f;
        } else {
            t -= (2.625f / 2.75f);
            return 7.5625f * t * t + 0.984375f;
        }
    }

    public static float easeOutScale(float t) {
        return (float) (1 - Math.pow(1 - t, 3));
    }
    public static float easeIn(float t) {
        return t * t;
    }
    public static float easeOut(float t) {
        return t * (2 - t);
    }
    public static float easeInOut(float t) {
        if (t < 0.5f) {
            return 2 * t * t;
        } else {
            return -1 + (4 - 2 * t) * t;
        }
    }
    public static float easeOutElastic(float t) {
        float p = 0.3f;
        if (t == 0 || t == 1) return t;
        float s = p / 4;
        t -= 1;
        return (float)(Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1);
    }
    public static float easeInBounce(float t) {
        return 1 - easeOutBounce(1 - t);
    }
    public static float easeInOutBounce(float t) {
        if (t < 0.5f) return easeInBounce(t * 2) * 0.5f;
        return easeOutBounce(t * 2 - 1) * 0.5f + 0.5f;
    }
}
