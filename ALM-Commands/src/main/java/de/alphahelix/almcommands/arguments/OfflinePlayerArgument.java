package de.alphahelix.almcommands.arguments;

import de.alphahelix.almcore.uuid.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerArgument extends Argument<OfflinePlayer> {
    @Override
    public boolean matches() {
        return UUIDFetcher.getUUID(getEnteredArgument()) != null;
    }

    @Override
    public OfflinePlayer fromArgument() {
        if (matches())
            return Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(getEnteredArgument()));
        return null;
    }
}
