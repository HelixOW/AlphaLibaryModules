package de.alphahelix.almschematic.serializer;

import com.google.gson.*;
import de.alphahelix.almschematic.Schematic;
import org.bukkit.Material;

import java.lang.reflect.Type;

public class LocationDiffSerializer implements JsonSerializer<Schematic.LocationDiff>, JsonDeserializer<Schematic.LocationDiff> {

    @Override
    public Schematic.LocationDiff deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = (JsonObject) jsonElement;

        Material mat = Material.getMaterial(obj.getAsJsonPrimitive("type").getAsString());
        byte data = obj.getAsJsonPrimitive("data").getAsByte();
        int x = obj.getAsJsonPrimitive("x").getAsInt();
        int y = obj.getAsJsonPrimitive("y").getAsInt();
        int z = obj.getAsJsonPrimitive("z").getAsInt();

        return new Schematic.LocationDiff() {
            @Override
            public Material getBlockType() {
                return mat;
            }

            @Override
            public byte getBlockData() {
                return data;
            }

            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public int getZ() {
                return z;
            }
        };
    }

    @Override
    public JsonElement serialize(Schematic.LocationDiff locationDiff, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", locationDiff.getBlockType().name());
        obj.addProperty("data", locationDiff.getBlockData());
        obj.addProperty("x", locationDiff.getX());
        obj.addProperty("y", locationDiff.getY());
        obj.addProperty("z", locationDiff.getZ());

        return obj;
    }
}
