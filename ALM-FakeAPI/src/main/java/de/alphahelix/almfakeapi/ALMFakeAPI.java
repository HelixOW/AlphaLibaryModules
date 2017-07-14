package de.alphahelix.almfakeapi;

import de.alphahelix.almcore.ALModule;
import de.alphahelix.almfakeapi.fakeapi.FakeAPI;
import de.alphahelix.almfakeapi.fakeapi.utils.GameProfileBuilder;

public final class ALMFakeAPI implements ALModule {

    private static GameProfileBuilder.GameProfileFile gameProfileFile;

    public static GameProfileBuilder.GameProfileFile getGameProfileFile() {
        return gameProfileFile;
    }

    @Override
    public void load() {
    }

    @Override
    public void enable() {
        FakeAPI.enable();
        gameProfileFile = new GameProfileBuilder.GameProfileFile();
    }

    @Override
    public void disable() {
    }
}
