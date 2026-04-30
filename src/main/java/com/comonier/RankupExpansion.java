package com.comonier;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankupExpansion extends PlaceholderExpansion {

    private final Rankup plugin;

    public RankupExpansion(Rankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rankup";
    }

    @Override
    public @NotNull String getAuthor() {
        return "comonier";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; 
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        int currentRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());
        RankManager.Rank rank = plugin.getRankManager().getRank(currentRankId);

        if (rank == null) return "";

        // Usamos ChatColor diretamente aqui para garantir que o PAPI envie a cor processada
        if (params.equalsIgnoreCase("tag")) {
            return ChatColor.translateAlternateColorCodes('&', rank.getTag());
        }

        if (params.equalsIgnoreCase("name")) {
            return ChatColor.translateAlternateColorCodes('&', rank.getDisplayName());
        }

        return null;
    }
}
