package de.alphahelix.almfakeapi;

import de.alphahelix.almcore.ALModule;
import de.alphahelix.almfakeapi.utils.GameProfileBuilder;

public final class ALMFakeAPI implements ALModule {

    private static GameProfileBuilder.GameProfileFile gameProfileFile;

    @Override
    public void enable() {
        gameProfileFile = new GameProfileBuilder.GameProfileFile();
    }

    public static GameProfileBuilder.GameProfileFile getGameProfileFile() {
        return gameProfileFile;
    }
}
