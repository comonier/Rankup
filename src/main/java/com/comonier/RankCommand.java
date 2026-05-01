package com.comonier;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class RankCommand implements CommandExecutor {

    private final Rankup plugin;

    public RankCommand(Rankup plugin) {
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
        } else {
            sendRankStatus(player, nextRank, currentRankId);
        }

        new RankMenu(plugin).open(player, 1);
        return true;
    }

    private void sendRankStatus(Player player, RankManager.Rank next, int currentId) {
        MessageManager mm = plugin.getMessageManager();
        RankManager rm = plugin.getRankManager();
        
        double currentMoney = Rankup.getEconomy().getBalance(player);
        RankManager.Rank currentRank = rm.getRank(currentId);
        
        String currentRankName = (currentRank != null) ? mm.format(currentRank.getDisplayName()) : "Nenhum";
        String nextRankName = mm.format(next.getDisplayName());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", player.getName());
        placeholders.put("%rank_display%", currentRankName);
        placeholders.put("%next_rank_display%", nextRankName);

        placeholders.put("%current_xp%", String.valueOf(player.getLevel()));
        placeholders.put("%req_xp%", String.valueOf(next.getReqXp()));
        placeholders.put("%missing_xp%", String.valueOf(Math.max(0, next.getReqXp() - player.getLevel())));

        // USANDO O FORMATADOR GLOBAL DO CONFIG
        placeholders.put("%current_money%", plugin.formatMoney(currentMoney));
        placeholders.put("%req_money%", plugin.formatMoney(next.getReqMoney()));
        placeholders.put("%missing_money%", plugin.formatMoney(Math.max(0, next.getReqMoney() - currentMoney)));

        placeholders.put("%current_time%", String.valueOf(rm.getPlayerPlayTimeMinutes(player)));
        placeholders.put("%req_time%", String.valueOf(next.getReqTime()));
        placeholders.put("%missing_time%", String.valueOf(Math.max(0, next.getReqTime() - rm.getPlayerPlayTimeMinutes(player))));

        player.sendMessage(mm.getMessage("rank-status.header"));
        player.sendMessage(mm.getMessage("rank-status.player-name").replace("%player%", player.getName()));
        player.sendMessage(mm.getMessage("rank-status.current-rank").replace("%rank_display%", currentRankName));
        player.sendMessage(mm.getMessage("rank-status.next-rank").replace("%next_rank_display%", nextRankName));
        
        player.sendMessage(mm.format(mm.getMessage("rank-status.xp"), placeholders));
        player.sendMessage(mm.format(mm.getMessage("rank-status.money"), placeholders));
        player.sendMessage(mm.format(mm.getMessage("rank-status.playtime"), placeholders));
        
        player.sendMessage(mm.getMessage("rank-status.footer"));
    }
}
