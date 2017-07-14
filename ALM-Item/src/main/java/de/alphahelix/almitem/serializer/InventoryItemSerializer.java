package de.alphahelix.almitem.serializer;

import com.google.gson.*;
import de.alphahelix.almitem.item.InventoryItem;
import de.alphahelix.almutils.utils.JSONUtil;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class InventoryItemSerializer implements JsonSerializer<InventoryItem>, JsonDeserializer<InventoryItem> {

    @Override
    public InventoryItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = (JsonObject) jsonElement;


        return new InventoryItem() {
            @Override
            public ItemStack getItemStack() {
                return JSONUtil.getGson().fromJson(obj.getAsJsonObject("item"), ItemStack.class);
            }

            @Override
            public int getSlot() {
                return obj.getAsJsonPrimitive("slot").getAsInt();
            }
        };
    }

    @Override
    public JsonElement serialize(InventoryItem inventoryItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.add("item", JSONUtil.getGson().toJsonTree(inventoryItem.getItemStack()));
        obj.addProperty("slot", inventoryItem.getSlot());

        return obj;
    }
}
