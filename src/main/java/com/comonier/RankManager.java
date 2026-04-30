package com.comonier;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
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
        if (id < 0) return null;
        if (ranks.containsKey(id)) return ranks.get(id);
        
        if (id <= 100) {
            return generateFallbackRank(id);
        }
        return null;
    }

    public Rank getNextRank(int currentId) {
        int nextId = currentId + 1;
        if (nextId > 100) return null;
        return getRank(nextId);
    }

    private Rank generateFallbackRank(int id) {
        Rank lastKnown = ranks.floorEntry(id) != null ? ranks.floorEntry(id).getValue() : 
                         new Rank(0, "&8[&9R&60&8]", "&8[&9R&60&8]", 0.0, 0, 0, new ArrayList<>(), new ArrayList<>());
        
        int diff = id - lastKnown.getId();
        
        // Progressão suave: soma 1000 de money e 5 de XP por nível faltante
        double moneyReq = lastKnown.getReqMoney() + (diff * 1000.0); 
        int xpReq = lastKnown.getReqXp() + (diff * 5);
        int timeReq = lastKnown.getReqTime() + (diff * 60);

        // Blocos de proteção acompanham o ID (Rank 2 = 2000 blocos)
        long claimBlocks = 1000L * id; 
        
        List<String> autoCmds = new ArrayList<>();
        autoCmds.add("adjustbonusclaimblocks %player% " + claimBlocks);
        
        List<String> autoPerms = new ArrayList<>();
        autoPerms.add("rank." + id);

        return new Rank(
            id,
            "&8[&9R&6" + id + "&8]",
            "&8[&9R&6" + id + "&8]",
            moneyReq,
            xpReq,
            timeReq,
            autoCmds,
            autoPerms
        );
    }

    public FileConfiguration getRanksConfig() {
        return ranksConfig;
    }

    public TreeMap<Integer, Rank> getRanks() {
        return ranks;
    }

    public int getPlayerPlayTimeMinutes(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
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
