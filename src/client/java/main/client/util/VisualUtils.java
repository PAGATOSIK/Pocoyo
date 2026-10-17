package main.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

public class VisualUtils {
    
    /**
     * Проверяет, является ли игрок PAGATOSIK
     */
    public static boolean isPagatosik(String playerName) {
        return "PAGATOSIK".equals(playerName);
    }
    
    /**
     * Проверяет, использует ли игрок PocoyoVisual (по наличию иконки в табе)
     * Это будет определяться через специальный тег или проверку в PlayerListEntry
     */
    public static boolean usesPocoyoVisual(PlayerListEntry entry) {
        // В реальности это может работать через проверку специальных тегов
        // или через отправку пакетов между клиентами с этим визуалом
        // Пока возвращаем false - логика будет добавлена позже
        return false;
    }
}
