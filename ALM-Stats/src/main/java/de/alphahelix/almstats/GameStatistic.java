package de.alphahelix.almstats;

import com.google.gson.JsonElement;

import java.io.Serializable;

public interface GameStatistic extends Serializable {
    String getName();

    JsonElement save();
}
