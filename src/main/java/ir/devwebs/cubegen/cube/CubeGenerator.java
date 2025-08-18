package ir.devwebs.cubegen.cube;

import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.data.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

    public CubeGenerator(Location origin, Integer cubeSize, Player player, Plugin plugin, String cubeName, String type){
        this.plugin = plugin;
        this.origin = origin;
        this.cubeSize = cubeSize;
        this.cubeName = cubeName;

        if(type.equalsIgnoreCase("place")){
            newCube(player);
        }
    }

    public void newCube(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin2.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

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
                if (section != null) {
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

                        loc.getBlock().setType(chosen);
                        cubeBlocksLocations.add(loc);
                    }
                }
            }
        }

        if (type.equalsIgnoreCase("protect")) {
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
                if (section != null) {
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

                        loc.getBlock().setType(chosen);
                        cubeBlocksLocations.add(loc);
                    }
                }

                int borderOffset = offset + 1;
                for (int x = -borderOffset; x <= borderOffset; x++) {
                    for (int y = -borderOffset; y <= borderOffset; y++) {
                        for (int z = -borderOffset; z <= borderOffset; z++) {
                            int count = 0;
                            if (Math.abs(x) == borderOffset) count++;
                            if (Math.abs(y) == borderOffset) count++;
                            if (Math.abs(z) == borderOffset) count++;

                            if (count >= 2) {
                                Location loc = origin.clone().add(x, y, z);
                                loc.getBlock().setType(Material.BEDROCK);
                                cubeBlocksLocations.add(loc);
                            }
                        }
                    }
                }
            }
        }
    }
}
