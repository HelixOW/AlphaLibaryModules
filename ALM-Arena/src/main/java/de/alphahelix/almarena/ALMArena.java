package de.alphahelix.almarena;

import de.alphahelix.almarena.arena.ArenaFile;
import de.alphahelix.almcore.ALModule;

public final class ALMArena implements ALModule {

    private static ArenaFile arenaFile;

    public static ArenaFile getArenaFile() {
        return arenaFile;
    }

    @Override
    public void load() {

    }

    @Override
    public void enable() {
        arenaFile = new ArenaFile();
    }

    @Override
    public void disable() {

    }
}
