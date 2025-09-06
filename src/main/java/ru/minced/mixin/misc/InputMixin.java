package ru.minced.mixin.misc;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Input.class)
public class InputMixin {
    @Shadow
    public PlayerInput playerInput;
    
    @Shadow
    public float movementForward;
    
    @Shadow
    public float movementSideways;
    
    @Unique
    public boolean pressingRight;
    
    @Unique
    public boolean pressingLeft;
    
    @Unique
    public boolean pressingBack;
    
    @Unique
    public boolean pressingForward;
    
    @Unique
    public boolean jumping;
    
    @Unique
    public boolean sneaking;
    
    @Unique
    void updateBooleanFields() {
        if (playerInput != null) {
            pressingForward = playerInput.forward();
            pressingBack = playerInput.backward();
            pressingLeft = playerInput.left();
            pressingRight = playerInput.right();
            jumping = playerInput.jump();
            sneaking = playerInput.sneak();
        }
    }
    
    @Unique
    public void setPlayerInput(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
        this.playerInput = new PlayerInput(forward, back, left, right, jump, sneak, sprint);
        updateBooleanFields();
    }
}
