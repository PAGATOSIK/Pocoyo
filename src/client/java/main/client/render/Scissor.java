package main.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

public class Scissor {
    public static void push() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void pop() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void set(int x, int y, int width, int height) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();

        // В современных версиях scaleFactor берется из объекта Window
        double scale = window.getScaleFactor();

        int scissorX = (int) (x * scale);
        int scissorY = (int) ((window.getScaledHeight() - (y + height)) * scale);
        int scissorWidth = (int) (width * scale);
        int scissorHeight = (int) (height * scale);

        GL11.glScissor(scissorX, scissorY, Math.max(0, scissorWidth), Math.max(0, scissorHeight));
    }
}