package ru.minced.client.feature.module.impl.render.nametags;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import ru.minced.client.util.IMinecraft;

public class ItemTagsRenderer implements IMinecraft {

    public static void renderItemTags(MatrixStack matrixStack) {
    }

    private static boolean shouldRenderTag(ItemEntity item) {
        return item != null && mc.world != null && item.isAlive();
    }
} 