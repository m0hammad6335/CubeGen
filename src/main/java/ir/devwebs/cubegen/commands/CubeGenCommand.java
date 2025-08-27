package ir.devwebs.cubegen.commands;

import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.cube.CubeGenerator;
import ir.devwebs.cubegen.data.PlayerDataManager;
import ir.devwebs.cubegen.gui.CubeSetting;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.List;

public class CubeGenCommand implements CommandExecutor, TabCompleter {

    CubeGen plugin = CubeGen.getPlugin();

    public String setChatColorPlugin(String str){
        return ChatColor.translateAlternateColorCodes('&',"&8[&f&lCube&b&lGen&8] " + str);
    }

    public String setChatColor(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage( setChatColorPlugin("&cOnly players can use this command!"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        FileConfiguration dataConfig = plugin.getConfig();

        // cg

        if (args.length == 0) {
            TextComponent sourceCode = new TextComponent(setChatColor("&bSource Code"));
            sourceCode.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/m0hammad6335/CubeGen"));
            sourceCode.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§fClick to open GitHub repository")));

            TextComponent finalMessage = new TextComponent("");
            finalMessage.addExtra(sourceCode);
            finalMessage.addExtra(new TextComponent(setChatColor(" &7click to open github.")));

            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(setChatColor("&b&lCubeGen &f- &3Available Commands"));
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(" ");
            player.sendMessage(setChatColor("&b/cg create <name> &7- Create a new cube"));
            player.sendMessage(setChatColor("&b/cg remove <name> &7- Remove an existing cube"));
            player.sendMessage(setChatColor("&b/cg setting <name> &7- Configure cube settings"));
            player.sendMessage(setChatColor("&b/cg list &7- List all your cubes"));
            player.sendMessage(setChatColor("&b/cg place <name> <x> <y> <z> &7- Place a cube at coordinates"));
            player.sendMessage(setChatColor("&b/cg unplace <name> <count> &7- Remove a placed cube"));
            player.sendMessage(" ");
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.spigot().sendMessage(finalMessage);
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            return true;
        }

        // cg create <name>

        if(args.length != 2 && args[0].equals("create")){
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg create [name]"));
            return true;
        }

        if(args.length == 2 && args[0].equals("create") && !args[1].isEmpty()){
            if(data.contains("cubes." + args[1])){
                player.sendMessage(setChatColorPlugin("&cThis name already exists!"));
                return true;
            }

            ConfigurationSection section = dataConfig.getConfigurationSection("setting.default_blocks");

            data.createSection("cubes." + args[1]);

            if(section != null){
                for(String s : section.getKeys(false)){
                    if (Material.matchMaterial(s) != null) {
                        data.set("cubes." + args[1] + ".setting.blocks." + s, section.getInt(s, 0));
                    }else{
                        player.sendMessage(setChatColorPlugin("&cError to get default blocks!"));
                    }
                }
            }else{
                player.sendMessage(setChatColorPlugin("&cError to get default blocks!"));
            }

            int size = dataConfig.getInt("setting.size.default");
            int sizeMin = dataConfig.getInt("setting.size.default.minimum");
            int sizeMax = dataConfig.getInt("setting.size.default.maximum");

            if(sizeMin < 3){
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &crequired minimum size +3!"));
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            if(size < sizeMin){
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &crequired default size +minimum!"));
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            if(size > sizeMax){
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &4error to get config!"));
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] &crequired default size -maximum!"));
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            data.set("cubes." + args[1] + ".setting.size", size);

            data.set("cubes." + args[1] + ".setting.type", "normal");
            data.set("cubes." + args[1] + ".placed.count", 0);
            playerDataManager.savePlayerConfig(uuid, data);
            player.sendMessage(setChatColorPlugin("&aYou have been create &b" + args[1] + "&a cube!" ));

            return true;
        }

        // cg remove <name>

        if(args.length != 2 && args[0].equals("remove")){
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg remove [name]"));
            return true;
        }

        if(args.length == 2 && args[0].equals("remove") && !args[1].isEmpty()){
            if(!data.contains("cubes." + args[1])){
                player.sendMessage(setChatColorPlugin("&cYou dont have cube with this name!"));
                return true;
            }

            data.set("cubes." + args[1], null);
            playerDataManager.savePlayerConfig(uuid, data);
            player.sendMessage(setChatColorPlugin("&aYou have been remove &b" + args[1] + "&a cube!" ));

            return true;
        }

        // cg setting <cube name>

        if (args.length == 1 && args[0].equalsIgnoreCase("setting")) {
            player.sendMessage(setChatColorPlugin("&cUsage: &b/cg setting [name]"));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setting")) {
            if (!data.contains("cubes." + args[1])) {
                player.sendMessage(setChatColorPlugin("&cYou don't have a cube with this name!"));
                return true;
            }

            new CubeSetting(player, args[1]);
            return true;
        }

        // cg list

        if(args.length != 1 && args[0].equals("list")){
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg list"));
            return true;
        }

        if(args.length == 1 && args[0].equals("list")){
            if(!data.contains("cubes")){
                player.sendMessage(setChatColorPlugin("&cYou dont have any cubes!"));

                return true;
            }

            ConfigurationSection section = data.getConfigurationSection("cubes");
            List<String> totalCubes = new ArrayList<>(section.getKeys(false));

            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(setChatColor("&b&lCubeGen &f- &3Your Cubes"));
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage("");

            for(int i = 0; i < totalCubes.size(); i++){
                String cubeName = totalCubes.get(i);
                Integer cubeCount = i + 1;

                TextComponent setting = new TextComponent(setChatColor("&7[&8setting&7]"));
                setting.setClickEvent(new  ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg setting " + cubeName));
                setting.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§fClick to configure " + cubeName)));

                TextComponent mainText =  new TextComponent(setChatColor("&7[&f" + cubeCount + "&7]&b " + cubeName + " "));
                mainText.addExtra(setting);

                player.spigot().sendMessage(mainText);
            }

            player.sendMessage("");
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(setChatColor("&7Total: &b" + totalCubes.size() + " &7cubes"));
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            
            return true;
        }

        // cg place <cube name> <x> <y> <z>

        if (args.length != 5 && args[0].equalsIgnoreCase("place")) {
            player.sendMessage(setChatColorPlugin("&cUsage: &b/cg place [name] [x] [y] [z]"));
            return true;
        }

        if(args.length == 5 && args[0].equalsIgnoreCase("place")){
            String cubeName = args[1];
            if (!data.contains("cubes." + cubeName)) {
                player.sendMessage(setChatColorPlugin("&cYou don't have a cube with this name!"));
                return true;
            }

            if (!args[2].matches("-?\\d+") || !args[3].matches("-?\\d+") || !args[4].matches("-?\\d+")) {
                player.sendMessage(setChatColorPlugin("&cCoordinates must be integers!"));
                return true;
            }

            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int z = Integer.parseInt(args[4]);

            int cubeSize = data.getInt("cubes." + cubeName + ".setting.size");
            int placedCount = data.getInt("cubes." + cubeName + ".placed.count");

            placedCount++;

            Location location = new Location(player.getWorld(), x, y, z);
            new CubeGenerator(location, cubeSize, player, plugin, cubeName, "place", placedCount);
        }

        // cg unplace <name> <placed count>

        if (args.length != 3 && args[0].equalsIgnoreCase("unplace")) {
            player.sendMessage(setChatColorPlugin("&cUsage: &b/cg unplace [name] [count]"));
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("unplace")) {
            if (!data.contains("cubes." + args[1])) {
                player.sendMessage(setChatColorPlugin("&cYou don't have a cube with this name!"));
                return true;
            }

            if(!data.contains("cubes." + args[1] + ".placed." + args[2]) && !args[2].matches("[1-9]\\d*")){
                player.sendMessage(setChatColorPlugin("&cYou don't have a cube with this place count!"));
                return true;
            }

            new CubeGenerator(null ,null,player,plugin,args[1], "unplace", Integer.parseInt(args[2]));
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        // cg <arg>

        if(args.length == 1){
            return Arrays.asList("create", "remove", "setting",  "list", "place", "unplace");
        }

        // cg remove <arg>

        if(args.length == 2 && args[0].equals("remove")){
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg setting <arg>

        if(args.length == 2 && args[0].equals("setting")){
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg place <arg>

        if(args.length == 2 && args[0].equals("place")){
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg unplace <arg> <arg>

        if(args.length == 2 && args[0].equals("unplace")){
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        if(args.length == 3 && args[0].equals("unplace")){
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes." +  args[1] + ".placed");
            ArrayList<String> sectionCount = new ArrayList<>(AllSectionCube.getKeys(false));
            sectionCount.remove("count");

            if(AllSectionCube != null){
                return sectionCount;
            }
        }

        return Collections.emptyList();
    }
}
