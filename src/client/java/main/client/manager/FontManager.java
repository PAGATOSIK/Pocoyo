package main.client.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import java.awt.Font;
import java.io.InputStream;

public class FontManager {

    // Сюда можно добавить кастомные обертки над текстурами (например, CustomFontRenderer)
    // Для базового старта определим метод загрузки TTF из ассетов мода

    public void init() {
        // Пример загрузки шрифта из resources/assets/pocoyovisual/fonts/font.ttf
        try {
            Identifier fontLocation = Identifier.of("pocoyovisual", "fonts/font.ttf");
            InputStream is = MinecraftClient.getInstance().getResourceManager()
                    .getResource(fontLocation)
                    .orElseThrow()
                    .getInputStream();

            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            Font clientFont = baseFont.deriveFont(18f); // Размер шрифта

            // Здесь должна быть генерация текстурного атласа для глифов (символов)

        } catch (Exception e) {
            System.err.println("Не удалось загрузить кастомный шрифт, используем ванильный.");
            e.printStackTrace();
        }
    }
}