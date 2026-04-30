package com.comonier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RankMenu {

    private final Rankup plugin;

    public RankMenu(Rankup plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, int page) {
        MessageManager mm = plugin.getMessageManager();
        
        String title = mm.format(mm.getMessage("menu.title")
                .replace("%page%", String.valueOf(page)));
        
        Inventory inv = Bukkit.createInventory(null, 54, title);

        int start = (page - 1) * 45;
        int end = start + 45;
        int slot = 0;

        int playerRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());

        for (Map.Entry<Integer, RankManager.Rank> entry : plugin.getRankManager().getRanks().entrySet()) {
            int rankId = entry.getKey();
            if (rankId < start || rankId >= end) continue;

            RankManager.Rank rank = entry.getValue();
            inv.setItem(slot, createRankItem(rank, playerRankId));
            slot++;
        }

        if (page > 1) {
            inv.setItem(45, createItem(Material.ARROW, mm.getMessage("menu.previous-page")));
        }
        if (plugin.getRankManager().getRanks().size() > end) {
            inv.setItem(53, createItem(Material.ARROW, mm.getMessage("menu.next-page")));
        }

        player.openInventory(inv);
    }

    private ItemStack createRankItem(RankManager.Rank rank, int playerRankId) {
        MessageManager mm = plugin.getMessageManager();
        Material material;
        String status;

        if (playerRankId >= rank.getId()) {
            material = Material.LIME_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-completed");
        } else if (playerRankId + 1 == rank.getId()) {
            material = Material.YELLOW_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-next");
        } else {
            material = Material.RED_STAINED_GLASS_PANE;
            status = mm.getMessage("menu.status-locked");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Aplica as cores no Nome do Rank
            meta.setDisplayName(mm.format(rank.getDisplayName()));

            List<String> lore = new ArrayList<>();
            // Aplica as cores no Status
            lore.add(mm.format(mm.getMessage("menu.item-lore-status").replace("%status%", status)));
            
            if (rank.getId() > 0) {
                lore.add("");
                lore.add(mm.format(mm.getMessage("menu.item-lore-req-title")));
                lore.add(mm.format(mm.getMessage("menu.item-lore-money").replace("%amount%", String.format("%.2f", rank.getReqMoney()))));
                lore.add(mm.format(mm.getMessage("menu.item-lore-xp").replace("%amount%", String.valueOf(rank.getReqXp()))));
                lore.add(mm.format(mm.getMessage("menu.item-lore-time").replace("%amount%", String.valueOf(rank.getReqTime()))));
                
                if (!rank.getCommands().isEmpty()) {
                    lore.add(mm.format("§7- Blocos de Proteção: §e+" + (rank.getId() * 1000)));
                }
            }
            
            meta.setLore(lore);
            // Remove as informações de "13 components" e metadados do item
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getMessageManager().format(name));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        return item;
    }
}
