package de.alphahelix.almitem;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

/**
 * Modified version of an {@link ItemStack} to save it inside a file
 */
public interface InventoryItem extends Serializable {
    ItemStack getItemStack();

    int getSlot();
}
