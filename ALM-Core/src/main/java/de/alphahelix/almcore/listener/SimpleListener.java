package de.alphahelix.almcore.listener;

import de.alphahelix.almcore.ALMCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class SimpleListener implements Listener {
    public SimpleListener() {
        Bukkit.getPluginManager().registerEvents(this, ALMCore.getInstance());
    }
}
