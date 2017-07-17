package de.alphahelix.almutils;

import org.bukkit.entity.Player;

import java.util.LinkedList;

public class PlayerList extends LinkedList<String> {
    public void add(Player p) {
        add(p.getName());
    }

    public boolean has(Player p) {
        return contains(p.getName());
    }

    public void addIfNotExisting(Player p) {
        if (!has(p))
            add(p);
    }

    public Player[] getPlayers() {
        return Util.makePlayerArray(this);
    }
}
