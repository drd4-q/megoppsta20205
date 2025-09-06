package ru.minced.mixin.accessor;


import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {

    @Accessor("lastSprinting")
    boolean getLastSprinting();

    @Accessor(value = "lastPitch")
    float getLastPitch();

    @Accessor(value = "lastYaw")
    float getLastYaw();
}
