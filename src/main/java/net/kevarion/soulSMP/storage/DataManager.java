package net.kevarion.soulSMP.storage;

import net.kevarion.soulSMP.SoulSMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataManager {

    private final File playerDataFile;
    private final FileConfiguration playerDataConfig;

    public DataManager(SoulSMP plugin) {
        playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");

        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the data at startup
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public FileConfiguration getPlayerDataConfig() {
        return playerDataConfig;
    }

    public void savePlayerDataConfig() {
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializePlayerDataConfig() {
        if (!playerDataConfig.contains("players")) {
            playerDataConfig.createSection("players");
            savePlayerDataConfig();
        }
    }

    public void reloadPlayerDataConfig() {
        try {
            playerDataConfig.load(playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}