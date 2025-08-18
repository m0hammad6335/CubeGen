package ir.devwebs.cubegen.listener;

import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.data.PlayerDataManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoin implements Listener {

    CubeGen plugin = CubeGen.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        if(!data.contains("username")) {
            data.set("username", player.getName());
            playerDataManager.savePlayerConfig(uuid, data);
        }
    }

}
