package ir.devwebs.cubegen;

import ir.devwebs.cubegen.commands.CubeGenCommand;
import ir.devwebs.cubegen.data.PlayerDataManager;
import ir.devwebs.cubegen.listener.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class CubeGen extends JavaPlugin {

    private static CubeGen plugin;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        saveConfig();

        getConfigMatch();

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

    public void getConfigMatch(){
        if(!getConfig().contains("setting")) {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &c cannot get setting contains."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(!getConfig().contains("setting.default_blocks")) {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &c cannot get default blocks contains."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(!getConfig().contains("setting.blocks")) {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &c cannot get blocks contains."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ConfigurationSection defaultBlocksSection =  getConfig().getConfigurationSection("setting.default_blocks");
        ConfigurationSection blocksSection =  getConfig().getConfigurationSection("setting.blocks");

        if(defaultBlocksSection == null){
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cdefault_blocks section is empty!"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(blocksSection == null){
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cblocks section is empty!"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        for(String block : blocksSection.getKeys(false)){
            if(Material.getMaterial(block) == null){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cblock " + block + " not match!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            Material material = Material.getMaterial(block);

            if(!material.isBlock()){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cblock " + block + " is not block!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            if(!material.isSolid()){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cblock " + block + " is not solid!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        for(String block : defaultBlocksSection.getKeys(false)){
            if(Material.getMaterial(block) == null){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cdefault block " + block + " not match!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            Material material = Material.getMaterial(block);

            if(!material.isBlock()){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cdefault block " + block + " is not block!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            if(!material.isSolid()){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &cdefault block " + block + " is not solid!"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

}
