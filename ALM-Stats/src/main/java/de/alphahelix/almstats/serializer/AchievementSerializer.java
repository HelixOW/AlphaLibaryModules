package de.alphahelix.almstats.serializer;

import com.google.gson.*;
import de.alphahelix.almitem.item.InventoryItem;
import de.alphahelix.almstats.achievements.Achievement;
import de.alphahelix.almutils.utils.JSONUtil;

import java.lang.reflect.Type;
import java.util.List;

public class AchievementSerializer implements JsonSerializer<Achievement>, JsonDeserializer<Achievement> {
    @Override
    public Achievement deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = (JsonObject) json;

        return new Achievement() {
            @Override
            public String getName() {
                return obj.getAsJsonPrimitive("name").getAsString();
            }

            @Override
            public InventoryItem getIcon() {
                return JSONUtil.getGson().fromJson(obj.getAsJsonObject("icon"), InventoryItem.class);
            }

            @Override
            public List<String> getDescription() {
                return JSONUtil.getGson().fromJson(obj.getAsJsonArray("description"), List.class);
            }
        };
    }

    @Override
    public JsonElement serialize(Achievement achievement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", achievement.getName());
        obj.add("icon", JSONUtil.getGson().toJsonTree(achievement.getIcon()));
        obj.add("description", JSONUtil.getGson().toJsonTree(achievement.getDescription()));

        return obj;
    }
}
