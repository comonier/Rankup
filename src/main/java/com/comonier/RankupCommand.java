package com.comonier;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RankupCommand implements CommandExecutor {

    private final Rankup plugin;

    public RankupCommand(Rankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;
        MessageManager mm = plugin.getMessageManager();
        
        int currentRankId = plugin.getDatabase().getPlayerRank(player.getUniqueId());
        RankManager.Rank nextRank = plugin.getRankManager().getNextRank(currentRankId);

        if (nextRank == null) {
            mm.sendMessage(player, "rankup.max-rank");
            return true;
        }

        double playerMoney = Rankup.getEconomy().getBalance(player);
        int playerXp = player.getLevel();
        int playerTime = plugin.getRankManager().getPlayerPlayTimeMinutes(player);

        if (playerMoney >= nextRank.getReqMoney() && 
            playerXp >= nextRank.getReqXp() && 
            playerTime >= nextRank.getReqTime()) {
            
            Rankup.getEconomy().withdrawPlayer(player, nextRank.getReqMoney());
            player.setLevel(playerXp - nextRank.getReqXp());

            plugin.getDatabase().setPlayerRank(player.getUniqueId(), nextRank.getId());

            // Processa cores para o nome do rank antes de usar em mensagens/anúncios
            String formattedRankName = mm.format(nextRank.getDisplayName());

            for (String cmd : nextRank.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }

            for (String perm : nextRank.getPermissions()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set " + perm);
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%rank_display%", formattedRankName);
            mm.sendMessage(player, "rankup.success", placeholders);

            if (plugin.getConfig().getBoolean("settings.broadcast-rankup")) {
                String globalMsg = mm.getMessage("broadcast.rankup")
                        .replace("%player%", player.getName())
                        .replace("%rank_display%", formattedRankName);
                // O prefixo já é adicionado pelo mm.getMessage ou mm.format
                Bukkit.broadcastMessage(globalMsg);
            }

            // O Webhook do Discord remove as cores (&) automaticamente para o texto do Discord
            plugin.getDiscordWebhook().sendRankupMessage(player.getName(), nextRank.getDisplayName().replaceAll("(?i)&[0-9A-FK-ORX]", ""));

        } else {
            mm.sendMessage(player, "rankup.requirements-not-met");
        }

        return true;
    }
}
