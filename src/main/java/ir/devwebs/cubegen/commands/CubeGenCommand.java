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

    public String setChatColorPlugin(String str) {
        return ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8] " + str);
    }

    public String setChatColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(setChatColorPlugin("&cOnly players can use this command!"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        FileConfiguration dataConfig = plugin.getConfig();

        // cg permission

        if (!player.hasPermission("cubegen.use")){
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

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
            player.sendMessage(setChatColor("&b/cg unplace <name> &7- Remove a placed cube"));
            player.sendMessage(" ");
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.spigot().sendMessage(finalMessage);
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            return true;
        }

        // cg create permission

        if (!player.hasPermission("cubegen.create") && args[0].equals("create")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

        // cg create <name>

        if (args.length != 2 && args[0].equals("create")) {
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg create [name]"));
            return true;
        }

        if (args.length == 2 && args[0].equals("create") && !args[1].isEmpty()) {
            if (data.contains("cubes." + args[1])) {
                player.sendMessage(setChatColorPlugin("&cThis name already exists!"));
                return true;
            }

            ConfigurationSection section = dataConfig.getConfigurationSection("setting.default_blocks");

            data.createSection("cubes." + args[1]);

            if (section != null) {
                for (String s : section.getKeys(false)) {
                    if (Material.matchMaterial(s) != null) {
                        data.set("cubes." + args[1] + ".setting.blocks." + s, section.getInt(s, 0));
                    } else {
                        player.sendMessage(setChatColorPlugin("&cError to get default blocks!"));
                    }
                }
            } else {
                player.sendMessage(setChatColorPlugin("&cError to get default blocks!"));
            }

            int size = dataConfig.getInt("setting.size.default");

            data.set("cubes." + args[1] + ".setting.size", size);
            data.set("cubes." + args[1] + ".setting.type", "normal");
            data.set("cubes." + args[1] + ".placed.count", 0);
            playerDataManager.savePlayerConfig(uuid, data);
            player.sendMessage(setChatColorPlugin("&aYou have been create &b" + args[1] + "&a cube!"));

            return true;
        }

        // cg remove permission

        if (!player.hasPermission("cubegen.remove") && args[0].equals("remove")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

        // cg remove <name>

        if (args.length != 2 && args[0].equals("remove")) {
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg remove [name]"));
            return true;
        }

        if (args.length == 2 && args[0].equals("remove") && !args[1].isEmpty()) {
            if (!data.contains("cubes." + args[1])) {
                player.sendMessage(setChatColorPlugin("&cYou dont have cube with this name!"));
                return true;
            }

            data.set("cubes." + args[1], null);
            playerDataManager.savePlayerConfig(uuid, data);
            player.sendMessage(setChatColorPlugin("&aYou have been remove &b" + args[1] + "&a cube!"));

            return true;
        }

        // cg setting permission

        if (!player.hasPermission("cubegen.setting") && args[0].equals("setting")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
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

        // cg list permission

        if (!player.hasPermission("cubegen.list") && args[0].equals("list")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

        // cg list

        if (args.length != 1 && args[0].equals("list")) {
            player.sendMessage(setChatColorPlugin("&cusage: &b/cg list"));
            return true;
        }

        if (args.length == 1 && args[0].equals("list")) {
            if (!data.contains("cubes")) {
                player.sendMessage(setChatColorPlugin("&cYou dont have any cubes!"));

                return true;
            }

            ConfigurationSection section = data.getConfigurationSection("cubes");
            List<String> totalCubes = new ArrayList<>(section.getKeys(false));

            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(setChatColor("&b&lCubeGen &f- &3Your Cubes"));
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage("");

            for (int i = 0; i < totalCubes.size(); i++) {
                String cubeName = totalCubes.get(i);
                Integer cubeCount = i + 1;

                TextComponent setting = new TextComponent(setChatColor("&7[&8setting&7]"));
                setting.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cg setting " + cubeName));
                setting.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§fClick to configure " + cubeName)));

                TextComponent mainText = new TextComponent(setChatColor("&7[&f" + cubeCount + "&7]&b " + cubeName + " "));
                mainText.addExtra(setting);

                player.spigot().sendMessage(mainText);
            }

            player.sendMessage("");
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));
            player.sendMessage(setChatColor("&7Total: &b" + totalCubes.size() + " &7cubes"));
            player.sendMessage(setChatColor("&b&m-----------------------------------------------------"));

            return true;
        }

        // cg place permission

        if (!player.hasPermission("cubegen.place") && args[0].equals("place")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

        // cg place <cube name> <x> <y> <z>

        if (args.length != 5 && args[0].equalsIgnoreCase("place")) {
            player.sendMessage(setChatColorPlugin("&cUsage: &b/cg place [name] [x] [y] [z]"));
            return true;
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("place")) {
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
            boolean placedResult = data.getBoolean("cubes." + cubeName + ".placedResult");

            if (placedResult) {
                player.sendMessage(setChatColorPlugin("&cYou have already placed this Cube!"));
                return true;
            }

            Location location = new Location(player.getWorld(), x, y, z);
            playerDataManager.savePlayerConfig(uuid, data);
            new CubeGenerator(location, cubeSize, player, plugin, cubeName, "place");
            return true;
        }

        // cg unplace permission

        if (!player.hasPermission("cubegen.unplace") && args[0].equals("unplace")) {
            player.sendMessage(setChatColorPlugin("&cyou don't have permission to use this command!"));
            return true;
        }

        // cg unplace <name>

        if (args.length != 2 && args[0].equalsIgnoreCase("unplace")) {
            player.sendMessage(setChatColorPlugin("&cUsage: &b/cg unplace [name]"));
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("unplace")) {
            if (!data.contains("cubes." + args[1])) {
                player.sendMessage(setChatColorPlugin("&cYou don't have a cube with this name!"));
                return true;
            }

            boolean placedResult = data.getBoolean("cubes." + args[1] + ".placedResult");

            if (!placedResult) {
                player.sendMessage(setChatColorPlugin("&cThis Cube not placed!"));
                return true;
            }

            playerDataManager.savePlayerConfig(uuid, data);
            new CubeGenerator(null, null, player, plugin, args[1], "unplace");
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;

        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        // cg <arg>

        if (args.length == 1) {
            List<String> cmds = new ArrayList<>();

            if (player.hasPermission("cubegen.create")) cmds.add("create");
            if (player.hasPermission("cubegen.remove")) cmds.add("remove");
            if (player.hasPermission("cubegen.setting")) cmds.add("setting");
            if (player.hasPermission("cubegen.list")) cmds.add("list");
            if (player.hasPermission("cubegen.place")) cmds.add("place");
            if (player.hasPermission("cubegen.unplace")) cmds.add("unplace");

            return cmds;
        }

        // cg remove <arg>

        if(args.length == 2 && args[0].equals("remove")){
            if(!player.hasPermission("cubegen.remove")) return Collections.emptyList();
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg setting <arg>

        if(args.length == 2 && args[0].equals("setting")){
            if(!player.hasPermission("cubegen.setting")) return Collections.emptyList();
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg place <arg>

        if(args.length == 2 && args[0].equals("place")){
            if(!player.hasPermission("cubegen.place")) return Collections.emptyList();
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        // cg unplace <arg> <arg>

        if(args.length == 2 && args[0].equals("unplace")){
            if(!player.hasPermission("cubegen.unplace")) return Collections.emptyList();
            ConfigurationSection AllSectionCube = data.getConfigurationSection("cubes");

            if(AllSectionCube != null){
                return new ArrayList<>(AllSectionCube.getKeys(false));
            }
        }

        return Collections.emptyList();
    }
}