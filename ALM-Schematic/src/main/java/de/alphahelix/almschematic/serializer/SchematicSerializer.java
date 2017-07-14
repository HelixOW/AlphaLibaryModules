package de.alphahelix.almschematic.serializer;

import com.google.gson.*;
import de.alphahelix.almschematic.schematics.Schematic;
import de.alphahelix.almutils.utils.JSONUtil;

import java.lang.reflect.Type;
import java.util.List;

public class SchematicSerializer implements JsonSerializer<Schematic>, JsonDeserializer<Schematic> {
    @Override
    public Schematic deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = (JsonObject) jsonElement;

        return new Schematic() {
            @Override
            public String getName() {
                return obj.getAsJsonPrimitive("name").getAsString();
            }

            @Override
            public List<LocationDiff> getBlocks() {
                return JSONUtil.getGson().fromJson(obj.getAsJsonArray("locationDiffs"), List.class);
            }
        };
    }

    @Override
    public JsonElement serialize(Schematic schematic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", schematic.getName());
        obj.add("locationDiffs", JSONUtil.getGson().toJsonTree(schematic.getBlocks()));

        return obj;
    }
}
