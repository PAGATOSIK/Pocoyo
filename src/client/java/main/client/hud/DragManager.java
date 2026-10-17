package main.client.hud;

public class DragManager {
    private HudElement draggingElement = null;
    private float dragX, dragY;

    public void update(int mouseX, int mouseY, boolean mousePressed) {
        if (draggingElement != null) {
            if (mousePressed) {
                draggingElement.setX(mouseX - dragX);
                draggingElement.setY(mouseY - dragY);
            } else {
                draggingElement = null; // Отпустили мышку
            }
        }
    }

    public boolean startDragging(HudElement element, int mouseX, int mouseY) {
        if (element.isHovered(mouseX, mouseY)) {
            draggingElement = element;
            dragX = mouseX - element.getX();
            dragY = mouseY - element.getY();
            return true;
        }
        return false;
    }
}