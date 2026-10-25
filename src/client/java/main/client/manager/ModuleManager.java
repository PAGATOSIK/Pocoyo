package main.client.manager;

import main.client.module.Module;
import main.client.modules.game.Fullbright;
import main.client.modules.game.WorldTime;
import main.client.modules.visual.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        Hud hud = new Hud();
        hud.setToggled(true);
        register(hud);

        register(new TargetEsp());
        register(new Radius());
        register(new Fullbright());
        register(new Cape());
        register(new JumpCircle());
        register(new WorldTime());

        Particle particle = new Particle();
        particle.setToggled(true);
        register(particle);
    }

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }
        return null;
    }
}