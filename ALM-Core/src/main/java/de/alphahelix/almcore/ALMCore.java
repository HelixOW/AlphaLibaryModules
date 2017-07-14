package de.alphahelix.almcore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class ALMCore extends JavaPlugin {

    private static ArrayList<ALModule> modules = new ArrayList<>();
    private static ALMCore instance;

    public static void registerModule(ALModule module) {
        modules.add(module);
    }

    public static ALMCore getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        loadModules();
    }

    @Override
    public void onEnable() {
        instance = this;
        enableModules();
    }

    @Override
    public void onDisable() {
        disableModules();
    }

    private void loadModules() {
        for (ALModule module : modules)
            module.load();
    }

    private void enableModules() {
        for (ALModule module : modules)
            module.enable();
    }

    private void disableModules() {
        for (ALModule module : modules)
            module.disable();
    }
}
