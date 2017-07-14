package de.alphahelix.almcore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class ALMCore extends JavaPlugin {

    private static ArrayList<ALModule> modules = new ArrayList<>();
    private static ALMCore instance;

	@Override
	public void onEnable () {
		instance = this;
	}

	public static void enableModules() {
	    for(ALModule module : modules) {
	        module.enable();
        }
    }

    public static void registerModule(ALModule module) {
        modules.add(module);
    }

    public static ALMCore getInstance() {
        return instance;
    }
}
