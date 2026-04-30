package com.comonier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankListener implements Listener {

    private final Rankup plugin;

    public RankListener(Rankup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Pega o ID do rank atual do jogador (ex: 4)
        int currentRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());

        // Loop que percorre do Rank 0 até o Rank atual do jogador
        for (int i = 0; i <= currentRankId; i++) {
            RankManager.Rank rank = plugin.getRankManager().getRank(i);
            
            if (rank != null && rank.getPermissions() != null) {
                for (String perm : rank.getPermissions()) {
                    // Aplica as permissões de todos os níveis que o jogador já conquistou
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                        "lp user " + player.getName() + " permission set " + perm);
                }
            }
        }
    }
}
