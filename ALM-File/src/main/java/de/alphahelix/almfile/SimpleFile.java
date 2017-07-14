/*
 * Copyright (C) <2017>  <AlphaHelixDev>
 *
 *       This program is free software: you can redistribute it under the
 *       terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.alphahelix.almfile;

import de.alphahelix.almcore.ALMCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SimpleFile extends YamlConfiguration {

    private File source = null;

    /**
     * Create a new {@link SimpleFile} inside the given path with the name 'name'
     *
     * @param path the path where the {@link File} should be created in
     * @param name the name which the {@link File} should have
     */
    public SimpleFile(String path, String name) {
        new File(path).mkdirs();
        source = new File(path, name);
        createIfNotExist();
        addValues();
    }

    /**
     * Create a new {@link SimpleFile} inside the plugin path with the name 'name'
     *
     * @param name the name which the file should have
     */
    public SimpleFile(String name) {
        source = new File(ALMCore.getInstance().getDataFolder().getPath(), name);
        createIfNotExist();
        addValues();
    }

    /**
     * Convert a normal {@link File} into a {@link SimpleFile}
     *
     * @param f the old File which you want to convert
     */
    public SimpleFile(File f) {
        source = f;
        createIfNotExist();
        addValues();
    }

    /**
     * Finish the setup of the {@link SimpleFile}
     */
    private void finishSetup() {
        try {
            load(source);
        } catch (Exception ignored) {

        }
    }

    /**
     * Overridden method to add new standard values to a config
     */
    public void addValues() {

    }

    /**
     * Create a new {@link SimpleFile} if it's not existing
     */
    private void createIfNotExist() {
        options().copyDefaults(true);
        if (source == null || !source.exists()) {
            try {
                assert source != null;
                source.createNewFile();
            } catch (IOException e) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            source.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLaterAsynchronously(ALMCore.getInstance(), 20);
            }
        }
        finishSetup();
    }

    /**
     * Get a colored {@link String}
     *
     * @param path the path inside this {@link SimpleFile}
     * @return the {@link String} with Colors
     */
    public String getColorString(String path) {
        if (!contains(path))
            return "";

        try {
            String toReturn = getString(path);
            return ChatColor.translateAlternateColorCodes('&', toReturn);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get a colored {@link ArrayList} out of this {@link SimpleFile}
     *
     * @param path the path inside this {@link SimpleFile}
     * @return the {@link ArrayList} with Colors
     */
    public ArrayList<String> getColorStringList(String path) {
        if (!configContains(path)) return new ArrayList<>();
        if (!isList(path)) return new ArrayList<>();

        try {
            ArrayList<String> tR = new ArrayList<>();
            for (String str : getStringList(path)) {
                tR.add(ChatColor.translateAlternateColorCodes('&', str));
            }
            return tR;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Saves a {@link List} of arguments inside this {@link SimpleFile} as Strings
     *
     * @param path          the path where the arguments should be saved at
     * @param listArguments the arguments to save
     */
    private void setArgumentList(String path, String... listArguments) {
        List<String> arguments = new ArrayList<>();

        Collections.addAll(arguments, listArguments);

        override(path, arguments);
    }

    /**
     * Add arguments inside this {@link SimpleFile}
     *
     * @param path      the path where the arguments should be saved at
     * @param arguments the arguments to add
     */
    public void addArgumentsToList(String path, String... arguments) {
        List<String> args = getStringList(path);

        for (String arg : arguments) {
            if (!args.contains(arg)) args.add(arg);
        }

        setArgumentList(path, args.toArray(new String[args.size()]));
    }

    /**
     * Remove arguments from this {@link SimpleFile}
     *
     * @param path      the path where the arguments should be saved at
     * @param arguments the arguments to remove
     */
    public void removeArgumentsFromList(String path, String... arguments) {
        List<String> args = getStringList(path);

        for (String arg : arguments) {
            if (args.contains(arg)) {
                args.remove(arg);
                break;
            }
        }

        if (!args.isEmpty())
            setArgumentList(path, args.toArray(new String[args.size()]));
        else
            override(path, null);
    }

    /**
     * Save a {@link HashMap} inside this {@link SimpleFile}
     * The keys/values are saved as a {@link String}
     *
     * @param path the path where the {@link HashMap} should be saved at
     * @param map  the {@link HashMap} to save
     */
    public <K, V> void setMap(String path, Map<K, V> map) {
        ArrayList<String> keyToValue = new ArrayList<>();

        for (K k : map.keySet()) {
            keyToValue.add(k.toString() + " <:> " + map.get(k).toString());
        }

        addArgumentsToList(path, keyToValue.toArray(new String[keyToValue.size()]));
    }

    /**
     * Gets a {@link HashMap} from this {@link SimpleFile}
     *
     * @param path the path where the {@link HashMap} is located at
     * @return the {@link HashMap} saved at this location
     */
    public HashMap<String, String> getMap(String path) {
        HashMap<String, String> map = new HashMap<>();

        for (String seri : getStringList(path)) {
            String k = seri.split(" <:> ")[0];
            String v = seri.split(" <:> ")[1];

            map.put(k, v);
        }

        return map;
    }

    /**
     * Checks if this {@link SimpleFile} contains a specific {@link String}
     *
     * @param toCheck {@link String} which might be inside this {@link SimpleFile}
     * @return whether or not this {@link SimpleFile} contains the {@link String}
     */
    public boolean configContains(String toCheck) {
        boolean cContains = false;

        for (String key : getKeys(true)) {
            if (key.equalsIgnoreCase(toCheck))
                cContains = true;
        }

        return cContains;
    }

    /**
     * Save and load this {@link SimpleFile}
     */
    public void save() {
        try {
            if (source == null) return;
            save(source);
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * Add a new value to this {@link SimpleFile}
     *
     * @param path  where the value should be saved at
     * @param value the value which you want to save
     */
    public void setDefault(String path, Object value) {
        if (path.contains("§")) {
            path = path.replaceAll("§", "&");
        }
        if (value instanceof String)
            value = ((String) value).replaceAll("§", "&");

        addDefault(path, value);

        save();
    }

    /**
     * Replaces a value inside this {@link SimpleFile}
     *
     * @param path  where the value is located at
     * @param value the new value which should be saved
     */
    public void override(String path, Object value) {
        if (value instanceof String)
            value = ((String) value).replaceAll("§", "&");

        if (configContains(path))
            set(path, value);
        else
            addDefault(path, value);
        save();
    }
}

