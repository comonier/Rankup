package com.comonier;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public class RankManager {

    private final Rankup plugin;
    private final TreeMap<Integer, Rank> ranks = new TreeMap<>();
    private FileConfiguration ranksConfig;

    public RankManager(Rankup plugin) {
        this.plugin = plugin;
        loadRanks();
    }

    public void loadRanks() {
        ranks.clear();
        File file = new File(plugin.getDataFolder(), "ranks.yml");
        ranksConfig = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = ranksConfig.getConfigurationSection("ranks");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    int id = Integer.parseInt(key);
                    String displayName = section.getString(key + ".display-name");
                    String tag = section.getString(key + ".tag");
                    
                    // Se o requisito não existir no arquivo, ele virá como 0.0 ou 0
                    double money = section.getDouble(key + ".requirements.money");
                    int xp = section.getInt(key + ".requirements.xp-levels");
                    int time = section.getInt(key + ".requirements.play-time-minutes");
                    
                    List<String> commands = section.getStringList(key + ".rewards.commands");
                    List<String> permissions = section.getStringList(key + ".rewards.permissions");
                    
                    ranks.put(id, new Rank(id, displayName, tag, money, xp, time, commands, permissions));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public Rank getRank(int id) {
        // Agora o plugin APENAS retorna o que estiver no Map (vindo do arquivo)
        return ranks.get(id);
    }

    public Rank getNextRank(int currentId) {
        // Busca o próximo ID que realmente existe no arquivo
        return getRank(currentId + 1);
    }

    public FileConfiguration getRanksConfig() {
        return ranksConfig;
    }

    public TreeMap<Integer, Rank> getRanks() {
        return ranks;
    }

    public int getPlayerPlayTimeMinutes(Player player) {
        // Cálculo do tempo (Ticks -> Segundos -> Minutos)
        return (int) (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60);
    }

    public static class Rank {
        private final int id;
        private final String displayName;
        private final String tag;
        private final double reqMoney;
        private final int reqXp;
        private final int reqTime;
        private final List<String> commands;
        private final List<String> permissions;

        public Rank(int id, String displayName, String tag, double reqMoney, int reqXp, int reqTime, List<String> commands, List<String> permissions) {
            this.id = id;
            this.displayName = displayName;
            this.tag = tag;
            this.reqMoney = reqMoney;
            this.reqXp = reqXp;
            this.reqTime = reqTime;
            this.commands = commands;
            this.permissions = permissions;
        }

        public int getId() { return id; }
        public String getDisplayName() { return displayName; }
        public String getTag() { return tag; }
        public double getReqMoney() { return reqMoney; }
        public int getReqXp() { return reqXp; }
        public int getReqTime() { return reqTime; }
        public List<String> getCommands() { return commands; }
        public List<String> getPermissions() { return permissions; }
    }
}
