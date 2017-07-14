package de.alphahelix.almutils.utils;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JSONUtil {

    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationSerializer())
                .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
                .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer());

        try {
            gsonBuilder.registerTypeAdapter(Class.forName("de.alphahelix.almstats.achievements.Achievement"), Class.forName("de.alphahelix.almstats.serializer.AchievementSerializer").newInstance());
        } catch (ReflectiveOperationException ignored) {}

        try {
            gsonBuilder.registerTypeAdapter(Class.forName("de.alphahelix.almschematic.schematic.Schematic$LocationDiff"), Class.forName("de.alphahelix.almschematic.schematics.LocationDiffSerializer").newInstance());
        } catch (ReflectiveOperationException ignored) {}

        try {
            gsonBuilder.registerTypeAdapter(Class.forName("de.alphahelix.almschematic.schematic.Schematic"), Class.forName("de.alphahelix.almschematic.schematics.SchematicSerializer").newInstance());
        } catch (ReflectiveOperationException ignored) {}

        gson = gsonBuilder.create();
    }

    public static <T> String toJson(T toConvert) {
        return gson.toJson(toConvert);
    }

    public static <T> T getValue(String json, Class<T> definy) {
        return gson.fromJson(json, definy);
    }

    public static Gson getGson() {
        return gson;
    }
}

class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;

        String world = object.has("world") ? object.getAsJsonPrimitive("world").getAsString() : Bukkit.getWorlds().get(0).getName();
        double x = object.getAsJsonPrimitive("x").getAsDouble(), y = object.getAsJsonPrimitive("y").getAsDouble(), z = object.getAsJsonPrimitive("z").getAsDouble();
        float yaw = object.getAsJsonPrimitive("yaw").getAsFloat(), pitch = object.getAsJsonPrimitive("pitch").getAsFloat();

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        if (src.getWorld() != null)
            result.addProperty("world", src.getWorld().getName());

        result.addProperty("x", src.getX());
        result.addProperty("y", src.getY());
        result.addProperty("z", src.getZ());
        result.addProperty("yaw", src.getYaw());
        result.addProperty("pitch", src.getPitch());

        return result;
    }
}

class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;

        Material type = Material.getMaterial(object.getAsJsonPrimitive("type").getAsString());
        int amount = object.getAsJsonPrimitive("amount").getAsInt();
        short durability = object.getAsJsonPrimitive("durability").getAsShort();

        String name = null;
        List<String> lore = null;
        Set<String> flags = null;


        if (object.has("meta")) {
            JsonObject meta = object.getAsJsonObject("meta");

            if (meta.has("displayName")) {
                name = meta.getAsJsonPrimitive("displayName").getAsString();
            }

            if (meta.has("lore")) {
                lore = JSONUtil.getGson().fromJson(meta.getAsJsonArray("lore"), List.class);
            }

            flags = JSONUtil.getGson().fromJson(meta.getAsJsonArray("flags"), Set.class);

        }

        ItemStack is = new ItemStack(type, amount, durability);
        ItemMeta im = is.getItemMeta();

        if (name != null)
            im.setDisplayName(name);

        if (lore != null)
            im.setLore(lore);

        if (flags != null)
            for (String flag : flags)
                im.addItemFlags(ItemFlag.valueOf(flag));

        JsonArray enchantments = object.getAsJsonArray("enchantments");

        for (int i = 0; i < enchantments.size(); i++) {
            im.addEnchant(Enchantment.getByName(enchantments.get(i).getAsJsonObject().getAsJsonPrimitive("id").getAsString()),
                    enchantments.get(i).getAsJsonObject().getAsJsonPrimitive("lvl").getAsInt(), true);
        }

        is.setItemMeta(im);

        return is;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        JsonArray enchantments = new JsonArray();

        for (Enchantment e : src.getEnchantments().keySet()) {
            JsonObject ench = new JsonObject();

            ench.addProperty("id", e.getName());
            ench.addProperty("lvl", src.getEnchantments().get(e));

            enchantments.add(ench);
        }

        result.addProperty("type", src.getType().name());
        result.addProperty("amount", src.getAmount());
        result.addProperty("durability", src.getDurability());
        result.add("enchantments", enchantments);

        if (src.hasItemMeta()) {
            JsonObject meta = new JsonObject();
            ItemMeta itemMeta = src.getItemMeta();

            if (itemMeta.hasDisplayName())
                meta.addProperty("displayName", itemMeta.getDisplayName());

            if (itemMeta.hasLore())
                meta.add("lore", JSONUtil.getGson().toJsonTree(itemMeta.getLore()));

            meta.add("flags", JSONUtil.getGson().toJsonTree(itemMeta.getItemFlags()));

            result.add("meta", meta);
        }

        return result;
    }
}

class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

    public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;
        UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
        String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
        GameProfile profile = new GameProfile(id, name);

        if (object.has("properties")) {
            for (Map.Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                profile.getProperties().put(prop.getKey(), prop.getValue());
            }
        }
        return profile;
    }

    public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (profile.getId() != null)
            result.add("id", context.serialize(profile.getId()));
        if (profile.getName() != null)
            result.addProperty("name", profile.getName());
        if (!profile.getProperties().isEmpty())
            result.add("properties", context.serialize(profile.getProperties()));
        return result;
    }

}