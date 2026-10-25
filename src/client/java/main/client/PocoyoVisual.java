package main.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;

import main.client.manager.*;
import main.client.hud.HudManager;
import main.client.manager.ComboManager;
import main.client.module.Module;
import main.client.modules.visual.TargetEsp;
import main.client.modules.visual.Particle;
import main.client.modules.visual.JumpCircle;

public class PocoyoVisual implements ClientModInitializer {
    public static PocoyoVisual INSTANCE;

    public final String NAME = "PocoyoVisual";
    public final String VERSION = "1.0.0";

    private static final int SAVE_INTERVAL_TICKS = 10 * 60 * 20;
    private int ticksElapsed = 0;

    private ModuleManager moduleManager;
    private EventManager eventManager;
    private CfgManager cfgManager; // Используем обновленный CfgManager для default.pocou
    private FontManager fontManager;
    private HudManager hudManager;
    private ThemeManager themeManager;
    private CommandManager commandManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        System.out.println("[PocoyoVisual] Mod initialized!");

        init();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            try {
                if (world != null && world.isClient() && entity != null) {
                    TargetEsp esp = getTargetEsp();
                    if (esp != null && esp.isToggled()) {
                        esp.onAttack(entity);
                    }
                    Particle particle = getParticle();
                    if (particle != null && particle.isToggled()) {
                        particle.onAttack(entity);
                    }
                    // Обновляем combo при попадании
                    ComboManager.getInstance().onHit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (client.player != null && client.world != null) {
                    TargetEsp esp = getTargetEsp();
                    if (esp != null && esp.isToggled()) {
                        esp.onTick();
                    }
                    Particle particle = getParticle();
                    if (particle != null && particle.isToggled()) {
                        particle.onTick();
                    }

                    ticksElapsed++;
                    if (ticksElapsed >= SAVE_INTERVAL_TICKS) {
                        ticksElapsed = 0;
                        if (cfgManager != null) {
                            cfgManager.saveConfig();
                            System.out.println("[PocoyoVisual] Auto-saved config to default.pocou");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (client.player != null && client.world != null) {
                    if (moduleManager != null) {
                        for (Module m : moduleManager.getModules()) {
                            if (m.isToggled()) {
                                m.onTick();
                            }
                        }
                    }

                    ticksElapsed++;
                    if (ticksElapsed >= SAVE_INTERVAL_TICKS) {
                        ticksElapsed = 0;
                        if (cfgManager != null) {
                            cfgManager.saveConfig();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            try {
                TargetEsp esp = getTargetEsp();
                if (esp != null && esp.isToggled()) {
                    float tickProgress = context.tickCounter() != null ? context.tickCounter().getTickProgress(false) : 1.0F;
                    esp.render3D(
                            context.matrixStack(),
                            context.consumers(),
                            tickProgress
                    );
                }
                Particle particle = getParticle();
                if (particle != null && particle.isToggled()) {
                    float tickDelta = net.minecraft.client.MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
                    particle.render(context.matrixStack(), context.consumers(), tickDelta);
                }
                JumpCircle jumpCircle = getJumpCircle();
                if (jumpCircle != null && jumpCircle.isToggled()) {
                    float tickDelta = net.minecraft.client.MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
                    jumpCircle.render(context.matrixStack(), context.consumers(), tickDelta);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            stop();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private TargetEsp getTargetEsp() {
        if (moduleManager == null || moduleManager.getModules() == null) return null;
        for (Module m : moduleManager.getModules()) {
            if (m instanceof TargetEsp) {
                return (TargetEsp) m;
            }
        }
        return null;
    }

    private Particle getParticle() {
        if (moduleManager == null || moduleManager.getModules() == null) return null;
        for (Module m : moduleManager.getModules()) {
            if (m instanceof Particle) {
                return (Particle) m;
            }
        }
        return null;
    }

    private JumpCircle getJumpCircle() {
        if (moduleManager == null || moduleManager.getModules() == null) return null;
        for (Module m : moduleManager.getModules()) {
            if (m instanceof JumpCircle) {
                return (JumpCircle) m;
            }
        }
        return null;
    }

    public void init() {
        this.eventManager = new EventManager();
        this.fontManager = new FontManager();
        this.themeManager = new ThemeManager();
        this.moduleManager = new ModuleManager();
        this.hudManager = new HudManager();
        this.cfgManager = new CfgManager(); // Инициализация CfgManager
        this.commandManager = new CommandManager(); // Инициализация CommandManager

        this.moduleManager.init();
        this.hudManager.init();
        this.cfgManager.loadConfig(); // Загрузка конфигурации при старте
    }

    public void stop() {
        if (this.cfgManager != null) {
            this.cfgManager.saveConfig(); // Сохранение конфигурации
        }
    }

    public ModuleManager getModuleManager() { return moduleManager; }
    public EventManager getEventManager() { return eventManager; }
    public CfgManager getCfgManager() { return cfgManager; }
    public FontManager getFontManager() { return fontManager; }
    public HudManager getHudManager() { return hudManager; }
    public ThemeManager getThemeManager() { return themeManager; }
    public CommandManager getCommandManager() { return commandManager; }
}