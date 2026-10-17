package main.client.render;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Text used by the client UI, kept separate from Minecraft's default font. */
public final class UiText {
    /** The bundled display font used by every custom client label. */
    private static final Identifier UI_FONT = Identifier.of("pocou", "sf-pro-text-11");

    private UiText() {
    }

    public static Text of(String value) {
        return Text.literal(value).setStyle(Style.EMPTY.withFont(UI_FONT));
    }
}
