package ir.devwebs.cubegen.data;

import ir.devwebs.cubegen.CubeGen;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {
    private final CubeGen plugin;

    public PlayerDataManager(CubeGen plugin) {
        this.plugin = plugin;
    }

    public File getPlayerFile(UUID uuid) {
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if(!folder.exists()) folder.mkdirs();
        return new File(folder, uuid.toString() + ".yml");
    }

    public YamlConfiguration getPlayerConfig(UUID uuid){
        return YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
    }

    public void savePlayerConfig(UUID uuid,  YamlConfiguration config){
        try{
            config.save(getPlayerFile(uuid));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
