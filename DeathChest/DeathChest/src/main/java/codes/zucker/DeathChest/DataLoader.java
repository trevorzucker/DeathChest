package codes.zucker.DeathChest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

class DataLoader { // ConfigurationLoader, DataLoader, and LangLoader all work the same.

    public static Map<String, Object> DataValues = new HashMap<String, Object>();
    static File dataFile;
    static YamlConfiguration config;

    public static void CreateFile() {
        dataFile = new File(Main.getPlugin(Main.class).getDataFolder() + "/data.yml");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
            }
    }

    public static void LoadDataFile() {
        CreateFile();
        config = YamlConfiguration.loadConfiguration(dataFile);
        
        for (String coords : config.getKeys(false)) {
            Location loc = Utils.deserializeLocation(coords);
            ConfigurationSection c = config.getConfigurationSection(coords);
            Block b = loc.getBlock();
            try {
                UUID uuid = UUID.fromString(c.getString("owner"));
                long expire = c.getLong("expire") + System.currentTimeMillis();    
                Inventory inv = BukkitSerialization.fromBase64(c.getString("inventory"));
                PlayerChest chest = new PlayerChest(inv, uuid, expire);
                Events.deathChest.put(b, chest);
                Bukkit.getLogger().info("Added death chest from data file");
            } catch (IOException e) {}
            
        }
        
    }

    public static void SaveDataFile() {
        config = new YamlConfiguration();
        for (Entry<Block, PlayerChest> entry : Events.deathChest.entrySet()) {
            Block b = entry.getKey();
            Location location = b.getLocation();

            ConfigurationSection c = config.createSection(Utils.serializeLocation(location));

            PlayerChest pc = entry.getValue();
            c.set("owner", pc.owner.toString());
            long expire = pc.expire - System.currentTimeMillis();
            c.set("expire", expire);
            c.set("inventory", BukkitSerialization.toBase64(pc.inventory));
        }

        try { config.save(dataFile); } catch (IOException e) { }
    }
}