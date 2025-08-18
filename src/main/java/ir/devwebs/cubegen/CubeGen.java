package ir.devwebs.cubegen;

import ir.devwebs.cubegen.commands.CubeGenCommand;
import ir.devwebs.cubegen.data.PlayerDataManager;
import ir.devwebs.cubegen.listener.PlayerJoin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class CubeGen extends JavaPlugin {

    private static CubeGen plugin;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        plugin = this;
        playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

        getServer().getPluginCommand("cubeGen").setExecutor(new CubeGenCommand());

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &bPlugin has been enabled!"));
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &bPlugin has been disabled!"));
    }

    public static CubeGen getPlugin() {
        return plugin;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

}
