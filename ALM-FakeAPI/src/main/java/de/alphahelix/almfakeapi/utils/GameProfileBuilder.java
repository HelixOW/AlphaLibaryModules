package de.alphahelix.almfakeapi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import de.alphahelix.almfakeapi.ALMFakeAPI;
import de.alphahelix.almfile.file.SimpleJSONFile;
import net.minecraft.server.v1_12_R1.GameProfileSerializer;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameProfileBuilder {

    private static final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
    private static JSONParser parser = new JSONParser();

    private static HashMap<UUID, CachedProfile> cache = new HashMap<>();

    private static long cacheTime = -1;

    /**
     * Don't run in main thread!
     * <p>
     * Fetches the GameProfile from the Mojang servers
     *
     * @param uuid The player uuid
     * @return The GameProfile
     * @throws IOException If something wents wrong while fetching
     * @see GameProfile
     */
    public static GameProfile fetch(UUID uuid) throws Exception {
        return fetch(uuid, false);
    }

    /**
     * Don't run in main thread!
     * <p>
     * Fetches the GameProfile from the Mojang servers
     *
     * @param uuid     The player uuid
     * @param forceNew If true the cache is ignored
     * @return The GameProfile
     * @throws IOException If something wents wrong while fetching
     * @see GameProfile
     */
    public static GameProfile fetch(UUID uuid, boolean forceNew) throws Exception {
        if (!forceNew && cache.containsKey(uuid) && cache.get(uuid).isValid()) {
            return cache.get(uuid).profile;
        } else if (ALMFakeAPI.getGameProfileFile().getProfile(uuid) != null)
            return ALMFakeAPI.getGameProfileFile().getProfile(uuid);
        else {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                GameProfile result = gson.fromJson(json, GameProfile.class);
                cache.put(uuid, new CachedProfile(result));

                ALMFakeAPI.getGameProfileFile().addProfile(result);

                return result;
            } else {
                if (!forceNew && cache.containsKey(uuid)) {
                    return cache.get(uuid).profile;
                }
                throw new Exception("Can't retrieve GameProfile for UUID " + uuid);
            }
        }
    }

    /**
     * Builds a GameProfile for the specified args
     *
     * @param name The name
     * @param skin The url from the skin image
     * @return A GameProfile built from the arguments
     * @see GameProfile
     */
    public static GameProfile getProfile(String name, String skin) {
        return getProfile(name, skin, null);
    }

    /**
     * Builds a GameProfile for the specified args
     *
     * @param name    The name
     * @param skinUrl Url from the skin image
     * @param capeUrl Url from the cape image
     * @return A GameProfile built from the arguments
     * @see GameProfile
     */
    public static GameProfile getProfile(String name, String skinUrl, String capeUrl) {
        UUID id = UUID.randomUUID();
        GameProfile profile = new GameProfile(id, name);
        boolean cape = capeUrl != null && !capeUrl.isEmpty();

        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID(id));
        args.add(name);
        args.add(skinUrl);
        if (cape) args.add(capeUrl);

        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])))));
        return profile;
    }

    /**
     * Sets the time as long as you want to keep the gameprofiles in cache (-1 = never remove it)
     *
     * @param time cache time (default = -1)
     */
    public static void setCacheTime(long time) {
        cacheTime = time;
    }

    private static class CachedProfile {

        private long timestamp = System.currentTimeMillis();
        private GameProfile profile;

        public CachedProfile(GameProfile profile) {
            this.profile = profile;
        }

        public boolean isValid() {
            return cacheTime < 0 || (System.currentTimeMillis() - timestamp) < cacheTime;
        }
    }

    public static class GameProfileFile extends SimpleJSONFile {
        public GameProfileFile() {
            super("plugins/AlphaLibary", "profiles.json");
        }

        public void addProfile(GameProfile profile) {
            addValuesToList("Profiles", profile);
        }

        public GameProfile getProfile(UUID owner) {
            if (getListValues("Profiles", GameProfile[].class) != null)
                for (GameProfile profile : getListValues("Profiles", GameProfile[].class)) {
                    if (profile.getId().equals(owner)) return profile;
                }
            return null;
        }
    }
}