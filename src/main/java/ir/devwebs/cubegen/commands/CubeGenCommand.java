package ir.devwebs.cubegen.commands;

import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.cube.CubeGenerator;
import ir.devwebs.cubegen.data.PlayerDataManager;
import ir.devwebs.cubegen.gui.CubeSetting;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

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

            data.createSection("cubes." + args[1]);
            data.set("cubes." + args[1] + ".setting.type", "normal");
            data.set("cubes." + args[1] + ".setting.size", 7);
            data.set("cubes." + args[1] + ".setting.blocks.STONE", 80);
            data.set("cubes." + args[1] + ".setting.blocks.COBBLESTONE", 20);
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

            for(int i = 0; i < totalCubes.size(); i++){
                String cubeName = totalCubes.get(i);
                Integer cubeCount = i + 1;

                player.sendMessage(setChatColorPlugin("&b" + "[" + "&f" + cubeCount + "&b" + "]" + "&b " + cubeName));
            }

            return true;
        }

        // cg place <cube name> <x> <y> <z>

        if (args.length != 5 || !args[0].equalsIgnoreCase("place")) {
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

            Location location = new Location(player.getWorld(), x, y, z);
            new CubeGenerator(location, cubeSize, player, plugin, cubeName, "place");
        }

        // cg

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

        return Collections.emptyList();
    }
}
