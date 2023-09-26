package me.zombieman.playerkitsplus.manager;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    public static final String DATA_FOLDER_NAME = "playerData";

    public static final ConcurrentHashMap<UUID, FileConfiguration> playerDataCache = new ConcurrentHashMap<>();

    public static void initDataFolder(PlayerKitsPlus plugin) {
        File playerDataFolder = new File(plugin.getDataFolder(), PlayerDataManager.DATA_FOLDER_NAME);
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public static FileConfiguration getPlayerDataConfig(PlayerKitsPlus plugin, Player player) {
        return getPlayerDataConfig(plugin, player.getUniqueId());
    }

    public static FileConfiguration getPlayerDataConfig(PlayerKitsPlus plugin, UUID uuid) {
        FileConfiguration data = getCached(uuid);
        if (data != null) return data;

        File playerFile = getPlayerFile(plugin, uuid);
        if (!playerFile.exists()) {
            createFile(plugin, uuid);
        }

        data = YamlConfiguration.loadConfiguration(playerFile);
        cache(uuid, data);

        return data;
    }

    public static void createFile(PlayerKitsPlus plugin, Player player) {
        createFile(plugin, player.getUniqueId());
    }

    public static void createFile(PlayerKitsPlus plugin, UUID uuid) {
        File playerFile = getPlayerFile(plugin, uuid);

        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePlayerData(PlayerKitsPlus plugin, Player player) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration data = getCached(playerUUID);
        File playerFile = getPlayerFile(plugin, playerUUID);

        try {
            if (data != null) data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static File getPlayerFile(PlayerKitsPlus plugin, UUID playerUUID) {
        return new File(plugin.getDataFolder(), DATA_FOLDER_NAME + "/" + playerUUID + ".yml");
    }

    private static FileConfiguration getCached(UUID uuid) {
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }
        return null;
    }

    private static void cache(UUID uuid, FileConfiguration data) {
        playerDataCache.put(uuid, data);
    }

    public static void cleanupCache(Player player) {
        playerDataCache.remove(player.getUniqueId());
    }

}
