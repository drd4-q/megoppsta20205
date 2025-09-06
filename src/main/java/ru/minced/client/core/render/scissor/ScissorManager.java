package ru.minced.client.core.render.scissor;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.core.render.scissor.api.Pool;

import java.util.Stack;

public class ScissorManager implements IMinecraft {
    Pool<Scissor> scissorPool = new Pool<>(Scissor::new);
    Stack<Scissor> scissorStack = new Stack<>();

    public void push(double x, double y, double width, double height) {
        int guiScale = mc.options.getGuiScale().getValue();
        
        Scissor currentScissor = scissorPool.get().copy();
        currentScissor.set(x / guiScale, y / guiScale, width / guiScale, height / guiScale);
        scissorStack.push(currentScissor);
        setScissor(currentScissor);
    }

    public void pop() {
        if (!scissorStack.isEmpty()) {
            scissorPool.free(scissorStack.pop());
            if (scissorStack.isEmpty()) {
                RenderSystem.disableScissor();
            } else {
                setScissor(scissorStack.peek());
            }
        }
    }

    private void setScissor(Scissor scissor) {
        int scaleFactor = (int) mc.getWindow().getScaleFactor();
        
        int x = (scissor.x * scaleFactor);
        int y = mc.getWindow().getHeight() - ((scissor.y + scissor.height) * scaleFactor);
        int width = (scissor.width * scaleFactor);
        int height = (scissor.height * scaleFactor);

        RenderSystem.enableScissor(x, y, width, height);
    }

    public static class Scissor {
        public int x, y;
        public int width, height;

        public void set(double x, double y, double width, double height) {
            this.x = Math.max(0, (int) Math.round(x));
            this.y = Math.max(0, (int) Math.round(y));
            this.width = Math.max(0, (int) Math.round(width));
            this.height = Math.max(0, (int) Math.round(height));
        }

        Scissor copy() {
            Scissor newScissor = new Scissor();
            newScissor.set(this.x, this.y, this.width, this.height);
            return newScissor;
        }
    }
}