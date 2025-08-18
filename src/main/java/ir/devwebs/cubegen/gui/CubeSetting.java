package ir.devwebs.cubegen.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import de.erethon.headlib.HeadLib;
import ir.devwebs.cubegen.CubeGen;
import ir.devwebs.cubegen.data.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CubeSetting {
    public static ChestGui gui;
    public static ChestGui typeGui;
    private ChestGui blocksGui;

    CubeGen plugin = CubeGen.getPlugin();

    public String setChatColorPlugin(String str){
        return ChatColor.translateAlternateColorCodes('&', "&8[&f&lCube&b&lGen&8]" + str);
    }

    public String setChatColor(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public CubeSetting(Player player, String cubeName) {
        gui = new ChestGui(3, setChatColor("Cube &4Setting"));
        StaticPane pane = new StaticPane(0, 0 ,9, 3);
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        ItemStack typeStack = new ItemStack(Material.ENDER_CHEST);
        ItemMeta typeMeta = typeStack.getItemMeta();
        if(typeMeta != null){
            typeMeta.setDisplayName(setChatColor("&bcube type"));
            typeStack.setItemMeta(typeMeta);
        }
        GuiItem typeItem = new GuiItem(typeStack, (e) -> {
            CubeType(player, cubeName);
        });
        pane.addItem(typeItem, 4, 1);

        ItemStack sizeStack = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta sizeMeta = sizeStack.getItemMeta();
        if(sizeMeta != null){
            sizeMeta.setDisplayName(setChatColor("&bcube size: &f" + data.getString("cubes." + cubeName + ".setting.size")));
            sizeStack.setItemMeta(sizeMeta);
        }
        GuiItem sizeItem = new GuiItem(sizeStack, (e) -> {
            int size =  data.getInt("cubes." + cubeName + ".setting.size");

            if(e.isLeftClick()){
                if(size < 26){
                    data.set("cubes." + cubeName + ".setting.size", size + 2);
                    playerDataManager.savePlayerConfig(uuid, data);
                    new CubeSetting(player, cubeName);

                }
            }

            if(e.isRightClick()){
                if(size > 6){
                    data.set("cubes." + cubeName + ".setting.size", size - 2);
                    playerDataManager.savePlayerConfig(uuid, data);
                    new CubeSetting(player, cubeName);
                }
            }

            gui.update();
        });
        pane.addItem(sizeItem, 2, 1);

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (y == 1 && (x == 0 || x == 8)) {
                    ItemStack emptyStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta emptyMeta = emptyStack.getItemMeta();
                    if (emptyMeta != null) {
                        emptyMeta.setDisplayName(setChatColor(" "));
                        emptyStack.setItemMeta(emptyMeta);
                    }
                    GuiItem emptyItem = new GuiItem(emptyStack);
                    pane.addItem(emptyItem, x, y);
                }

                if(y != 1){
                    ItemStack emptyStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta emptyMeta = emptyStack.getItemMeta();
                    if (emptyMeta != null) {
                        emptyMeta.setDisplayName(setChatColor(" "));
                        emptyStack.setItemMeta(emptyMeta);
                    }
                    GuiItem emptyItem = new GuiItem(emptyStack);
                    pane.addItem(emptyItem, x, y);
                }
            }
        }

        ItemStack blocksStack = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta blocksMeta = blocksStack.getItemMeta();
        if(blocksMeta != null){
            blocksMeta.setDisplayName(setChatColor("&bcube blocks"));
            blocksStack.setItemMeta(blocksMeta);
        }
        GuiItem blocksItem = new GuiItem(blocksStack, (e) -> {
            CubeBlocks(player, cubeName, 0);
        });
        pane.addItem(blocksItem, 6, 1);

        gui.addPane(pane);
        gui.show(player);
    }

    public void CubeType(Player player, String cubeName) {
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        typeGui = new ChestGui(3, setChatColor("Cube &4Type"));
        StaticPane pane = new StaticPane(0, 0 ,9, 3);

        ItemStack typeStackStone = new ItemStack(Material.STONE);
        ItemMeta typeMetaStone = typeStackStone.getItemMeta();
        if(typeMetaStone != null){
            typeMetaStone.setDisplayName(setChatColor("&c&lnormal"));
            typeStackStone.setItemMeta(typeMetaStone);
        }

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (y == 1 && (x == 0 || x == 8)) {
                    ItemStack emptyStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta emptyMeta = emptyStack.getItemMeta();
                    if (emptyMeta != null) {
                        emptyMeta.setDisplayName(setChatColor(" "));
                        emptyStack.setItemMeta(emptyMeta);
                    }
                    GuiItem emptyItem = new GuiItem(emptyStack);
                    pane.addItem(emptyItem, x, y);
                }

                if(y != 1){
                    ItemStack emptyStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta emptyMeta = emptyStack.getItemMeta();
                    if (emptyMeta != null) {
                        emptyMeta.setDisplayName(setChatColor(" "));
                        emptyStack.setItemMeta(emptyMeta);
                    }
                    GuiItem emptyItem = new GuiItem(emptyStack);
                    pane.addItem(emptyItem, x, y);
                }
            }
        }

        ItemStack typeStackBedrock = new ItemStack(Material.BEDROCK);
        ItemMeta typeMetaBedrock = typeStackBedrock.getItemMeta();
        if(typeMetaBedrock != null){
            typeMetaBedrock.setDisplayName(setChatColor("&c&lprotect"));
            typeStackBedrock.setItemMeta(typeMetaBedrock);
        }

        GuiItem typeItemStone = new GuiItem(typeStackStone, (e) -> {
            data.set("cubes." + cubeName + ".setting.type", "normal");
            playerDataManager.savePlayerConfig(uuid, data);
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(setChatColorPlugin(" &bcube " + cubeName + " has been set to normal!"));
        });
        pane.addItem(typeItemStone, 3, 1);

        GuiItem typeItemBedrock = new GuiItem(typeStackBedrock, (e) -> {
            data.set("cubes." + cubeName + ".setting.type", "protect");
            playerDataManager.savePlayerConfig(uuid, data);
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(setChatColorPlugin(" &bcube " + cubeName + " has been set to protect!"));
        });
        pane.addItem(typeItemBedrock, 5, 1);

        typeGui.setOnGlobalDrag(e -> {
            e.setCancelled(true);
        });

        typeGui.setOnGlobalClick(e -> {
            e.setCancelled(true);
        });

        typeGui.addPane(pane);
        typeGui.show(player);
    }

    public void CubeBlocks(Player player, String cubeName, int page) {
        UUID uuid = player.getUniqueId();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration data = playerDataManager.getPlayerConfig(uuid);

        ChestGui blocksGui = new ChestGui(6, setChatColor("Cube &4Blocks"));
        StaticPane pane = new StaticPane(0, 0, 9, 6);

        List<Material> allBlocks = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(mat -> mat.isSolid() && mat.isOccluding())
                .collect(Collectors.toList());

        int pageSize = 36;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allBlocks.size());

        int slot = 0;
        for (int i = start; i < end; i++) {
            Material mat = allBlocks.get(i);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String path = "cubes." + cubeName + ".setting.blocks." + mat.name();

            int chanceBlock = data.getInt(path, 0);
            if (chanceBlock > 0) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.setLore(Arrays.asList(
                    setChatColor("&bchance: &f" + chanceBlock),
                    "",
                    setChatColor("&7Shift Left click to add 1 chance"),
                    setChatColor("&7Shift Right click to subtract 1 chance")
            ));
            item.setItemMeta(meta);

            int x = slot % 9;
            int y = slot / 9;

            pane.addItem(new GuiItem(item, e -> {
                e.setCancelled(true);
                ItemStack clicked = e.getCurrentItem();
                ItemMeta clickedMeta = clicked.getItemMeta();
                int chance = data.getInt(path, 0);

                if (!e.isShiftClick()) {
                    if (chance > 0) {
                        chance = 0;
                        clickedMeta.removeEnchant(Enchantment.DURABILITY);
                    } else {
                        chance = 100;
                        clickedMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                        clickedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                }

                if (e.isShiftClick()) {
                    if (e.isRightClick() && chance < 100) {
                        if(clickedMeta.hasEnchant(Enchantment.DURABILITY)) {
                            chance++;
                        }else{
                            player.sendMessage(setChatColor("&c&lHey! &7first select your block."));
                        }
                    }
                    if (e.isLeftClick() && chance > 1) {
                        if(clickedMeta.hasEnchant(Enchantment.DURABILITY)) {
                            chance--;
                        }else{
                            player.sendMessage(setChatColor("&c&lHey! &7first select your block."));
                        }
                    }
                }

                data.set(path, chance > 0 ? chance : null);
                playerDataManager.savePlayerConfig(uuid, data);

                clickedMeta.setLore(Arrays.asList(
                        setChatColor("&bchance: &f" + chance),
                        "",
                        setChatColor("&7Shift Left click to add 1 chance"),
                        setChatColor("&7Shift Right click to subtract 1 chance")
                ));

                clicked.setItemMeta(clickedMeta);
            }), x, y);

            slot++;
        }

        for (int i = 45; i < 54; i++) {
            ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta metaGlass = glass.getItemMeta();
            metaGlass.setDisplayName(" ");
            glass.setItemMeta(metaGlass);
            pane.addItem(new GuiItem(glass, e -> e.setCancelled(true)), i % 9, i / 9);
        }

        ItemStack infoItem = HeadLib.STONE_EXCLAMATION_MARK.toItemStack();
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(setChatColor("&6Information"));
        infoMeta.setLore(Arrays.asList(
                setChatColor("&7Left click to select a block"),
                setChatColor("&7Click again to remove it"),
                setChatColor("&7Default chance = 100")
        ));
        infoItem.setItemMeta(infoMeta);
        pane.addItem(new GuiItem(infoItem, e -> e.setCancelled(true)), 4, 5);

        ItemStack leftArrow = HeadLib.STONE_ARROW_LEFT.toItemStack();
        ItemMeta leftMeta = leftArrow.getItemMeta();
        leftMeta.setDisplayName(setChatColor("&aPrevious Page"));
        leftArrow.setItemMeta(leftMeta);
        pane.addItem(new GuiItem(leftArrow, e -> {
            e.setCancelled(true);
            if (page > 0) {
                CubeBlocks(player, cubeName, page - 1);
            }
        }), 3, 5);

        ItemStack rightArrow = HeadLib.STONE_ARROW_RIGHT.toItemStack();
        ItemMeta rightMeta = rightArrow.getItemMeta();
        rightMeta.setDisplayName(setChatColor("&aNext Page"));
        rightArrow.setItemMeta(rightMeta);
        pane.addItem(new GuiItem(rightArrow, e -> {
            e.setCancelled(true);
            if ((page + 1) * pageSize < allBlocks.size()) {
                CubeBlocks(player, cubeName, page + 1);
            }
        }), 5, 5);

        blocksGui.setOnGlobalDrag(e -> e.setCancelled(true));
        blocksGui.setOnGlobalClick(e -> e.setCancelled(true));

        blocksGui.addPane(pane);
        blocksGui.show(player);
    }
}