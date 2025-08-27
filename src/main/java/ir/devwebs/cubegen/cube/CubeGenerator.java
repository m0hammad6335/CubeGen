package ir.devwebs.cubegen.cube;

import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.data.PlayerDataManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CubeGenerator {
    CubeGen plugin2 = CubeGen.getPlugin();

    public Location origin;
    public Plugin plugin;
    public Integer cubeSize;
    public String cubeName;
    public List<Location> cubeBlocksLocations = new ArrayList<>();
    public Integer placedCount;

    public String setChatColorPlugin(String str){
        return ChatColor.translateAlternateColorCodes('&',"&8[&f&lCube&b&lGen&8] " + str);
    }

    public String setChatColor(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public CubeGenerator(Location origin, Integer cubeSize, Player player, Plugin plugin, String cubeName, String type, Integer placedCount){
        this.plugin = plugin;
        this.origin = origin;
        this.cubeSize = cubeSize;
        this.cubeName = cubeName;
        this.placedCount = placedCount;

        if(type.equalsIgnoreCase("place")){
            place(player);
        }

        if(type.equalsIgnoreCase("unplace")){
            unplace(player);
        }
    }

    public void unplace (Player player){
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin2.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        data.set("test", 1);

        ConfigurationSection section1 = data.getConfigurationSection("cubes." + cubeName + ".placed." + placedCount + ".location1");
        ConfigurationSection section2 = data.getConfigurationSection("cubes." + cubeName + ".placed." + placedCount + ".location2");

        boolean replay = false;

        if(section1 != null && !section1.getKeys(false).isEmpty()){
            for(String key : section1.getKeys(false)){
                int x = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location1." + key + ".x");
                int y = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location1." + key + ".y");
                int z = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location1." + key + ".z");
                String worldName = data.getString("cubes." + cubeName + ".placed." + placedCount + ".location1." + key + ".world");

                World world = Bukkit.getWorld(worldName);

                if(world != null){
                    Location loc = new Location(world, x,y,z);
                    loc.getBlock().setType(Material.AIR);
                }

                if(!replay){
                    player.sendMessage(setChatColorPlugin("&bSuccess unplace cube!"));
                    replay = true;
                }
            }
        }

        if(section2 != null && !section2.getKeys(false).isEmpty()){
            for(String key : section2.getKeys(false)){
                int x = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location2." + key + ".x");
                int y = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location2." + key + ".y");
                int z = data.getInt("cubes." + cubeName + ".placed." + placedCount + ".location2." + key + ".z");
                String worldName = data.getString("cubes." + cubeName + ".placed." + placedCount + ".location2." + key + ".world");

                World world = Bukkit.getWorld(worldName);

                if(world != null){
                    Location loc = new Location(world, x,y,z);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }

        data.set("cubes." + cubeName + ".placed." + placedCount, null);
        playerDataManager.savePlayerConfig(uuid, data);
    }

    public void place(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin2.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        data.set("cubes." + cubeName + ".placed.count", placedCount);

        cubeBlocksLocations.clear();

        String type = data.getString("cubes." + cubeName + ".setting.type", "normal");

        if (type.equalsIgnoreCase("normal")) {
            int offset = cubeSize / 2;
            List<Location> allLocations = new ArrayList<>();

            for (int x = -offset; x <= offset; x++) {
                for (int y = -offset; y <= offset; y++) {
                    for (int z = -offset; z <= offset; z++) {
                        Location loc = origin.clone().add(x, y, z);
                        allLocations.add(loc);
                    }
                }
            }

            if (data.contains("cubes." + cubeName + ".setting.blocks")) {
                var section = data.getConfigurationSection("cubes." + cubeName + ".setting.blocks");
                if (section != null && !section.getKeys(false).isEmpty()) {
                    List<Material> materials = new ArrayList<>();
                    List<Integer> weights = new ArrayList<>();
                    int totalWeight = 0;

                    for (String key : section.getKeys(false)) {
                        int chance = data.getInt("cubes." + cubeName + ".setting.blocks." + key, 0);
                        Material mat = Material.matchMaterial(key);
                        if (mat != null && chance > 0) {
                            materials.add(mat);
                            weights.add(chance);
                            totalWeight += chance;
                        }
                    }

                    Random random = new Random();
                    boolean error = false;

                    for (Location loc : allLocations) {
                        if(loc.getBlock().getType() != Material.AIR){
                            error = true;
                            break;
                        }
                    }

                    if(!error){
                        int index = 0;
                        for (Location loc : allLocations) {
                            int roll = random.nextInt(totalWeight) + 1;
                            int current = 0;
                            Material chosen = Material.STONE;

                            for (int i = 0; i < materials.size(); i++) {
                                current += weights.get(i);
                                if (roll <= current) {
                                    chosen = materials.get(i);
                                    break;
                                }
                            }

                            cubeBlocksLocations.add(loc);

                            loc.getBlock().setType(chosen);
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".x", loc.getBlockX());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".y", loc.getBlockY());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".z", loc.getBlockZ());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".world", loc.getWorld().getName());
                            index++;
                        }
                        player.sendMessage(setChatColorPlugin("&b success place " + placedCount + " cube " + cubeName + "."));
                    }else{
                        player.sendMessage(setChatColorPlugin("&b you cant place cube in other blocks."));
                    }
                }else{
                    player.sendMessage(setChatColorPlugin("&b you do not set blocks for cube."));
                }
            }
        }

        if (type.equalsIgnoreCase("protect")) {
            int offset = cubeSize / 2;
            boolean checkError = false;
            List<Location> allLocations = new ArrayList<>();

            for (int x = -offset; x <= offset; x++) {
                for (int y = -offset; y <= offset; y++) {
                    for (int z = -offset; z <= offset; z++) {
                        Location loc = origin.clone().add(x, y, z);
                        allLocations.add(loc);
                    }
                }
            }

            if (data.contains("cubes." + cubeName + ".setting.blocks")) {
                var section = data.getConfigurationSection("cubes." + cubeName + ".setting.blocks");
                if (section != null && !section.getKeys(false).isEmpty()) {
                    List<Material> materials = new ArrayList<>();
                    List<Integer> weights = new ArrayList<>();
                    int totalWeight = 0;

                    for (String key : section.getKeys(false)) {
                        int chance = data.getInt("cubes." + cubeName + ".setting.blocks." + key, 0);
                        Material mat = Material.matchMaterial(key);
                        if (mat != null && chance > 0) {
                            materials.add(mat);
                            weights.add(chance);
                            totalWeight += chance;
                        }
                    }

                    Random random = new Random();
                    boolean error = false;

                    for (Location loc : allLocations) {
                        if(loc.getBlock().getType() != Material.AIR){
                            error = true;
                            break;
                        }
                    }

                    if(!error){
                        int index = 0;
                        for (Location loc : allLocations) {
                            int roll = random.nextInt(totalWeight) + 1;
                            int current = 0;
                            Material chosen = Material.STONE;

                            for (int i = 0; i < materials.size(); i++) {
                                current += weights.get(i);
                                if (roll <= current) {
                                    chosen = materials.get(i);
                                    break;
                                }
                            }

                            cubeBlocksLocations.add(loc);

                            loc.getBlock().setType(chosen);
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".x", loc.getBlockX());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".y", loc.getBlockY());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".z", loc.getBlockZ());
                            data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location1." + String.valueOf(index) + ".world", loc.getWorld().getName());
                            index++;
                        }
                        player.sendMessage(setChatColorPlugin("&b success place " + placedCount + " cube " + cubeName + "."));
                    }else{
                        checkError = true;
                        player.sendMessage(setChatColorPlugin("&b you cant place cube in other blocks."));
                    }
                }else{
                    player.sendMessage(setChatColorPlugin("&b you do not set blocks for cube."));
                }

                int borderOffset = offset + 1;
                int borderIndex = 0;
                for (int x = -borderOffset; x <= borderOffset; x++) {
                    for (int y = -borderOffset; y <= borderOffset; y++) {
                        for (int z = -borderOffset; z <= borderOffset; z++) {
                            int count = 0;
                            if (Math.abs(x) == borderOffset) count++;
                            if (Math.abs(y) == borderOffset) count++;
                            if (Math.abs(z) == borderOffset) count++;

                            if (count >= 2) {
                                Location loc = origin.clone().add(x, y, z);
                                cubeBlocksLocations.add(loc);

                                if(!checkError){
                                    if (loc.getBlock().getType().isAir()) {
                                        loc.getBlock().setType(Material.BEDROCK);

                                        data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location2." + String.valueOf(borderIndex) + ".x", loc.getBlockX());
                                        data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location2." + String.valueOf(borderIndex) + ".y", loc.getBlockY());
                                        data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location2." + String.valueOf(borderIndex) + ".z", loc.getBlockZ());
                                        data.set("cubes." + cubeName + ".placed." + String.valueOf(placedCount) + ".location2." + String.valueOf(borderIndex) + ".world", loc.getWorld().getName());
                                        borderIndex++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        playerDataManager.savePlayerConfig(uuid, data);
    }
}
