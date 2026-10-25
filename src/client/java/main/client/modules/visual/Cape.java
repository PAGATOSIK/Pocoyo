package main.client.modules.visual;

import main.client.module.Category;
import main.client.module.Module;
import net.minecraft.util.Identifier;

public class Cape extends Module {
    private static final Identifier CAPE_TEXTURE = Identifier.of("pocou", "texture/cape.png");

    public Cape() {
        super("Cape", Category.VISUAL, "отображает плащ на игроке");
    }

    public Identifier getCapeTexture() {
        return CAPE_TEXTURE;
    }
}
