package com.comonier;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Rankup extends JavaPlugin {

    private static Rankup instance;
    private static Economy econ = null;
    
    private MessageManager messageManager;
    private RankManager rankManager;
    private Database database;
    private DiscordWebhook discordWebhook;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Desativado por falta de dependência: Vault!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        createCustomFiles();
        
        this.messageManager = new MessageManager(this);
        this.rankManager = new RankManager(this);
        this.database = new Database(this);
        this.discordWebhook = new DiscordWebhook(this);

        RankTabCompleter tabCompleter = new RankTabCompleter();

        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("rank").setTabCompleter(tabCompleter);

        getCommand("rankup").setExecutor(new RankupCommand(this));
        getCommand("rankup").setTabCompleter(tabCompleter);
        
        getCommand("rankreload").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("rankup.admin")) {
                sender.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            reloadConfig();
            messageManager.loadMessages();
            rankManager.loadRanks();
            sender.sendMessage(messageManager.getMessage("plugin-reloaded"));
            return true;
        });
        getCommand("rankreload").setTabCompleter(tabCompleter);

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandProtectionListener(), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RankupExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void createCustomFiles() {
        List<String> files = Arrays.asList(
                "messages_pt.yml", "messages_en.yml", "messages_es.yml", "messages_ru.yml", "ranks.yml"
        );
        for (String fileName : files) {
            File file = new File(getDataFolder(), fileName);
            if (!file.exists()) {
                saveResource(fileName, false);
            }
        }
    }

    public static Rankup getInstance() { return instance; }
    public static Economy getEconomy() { return econ; }
    public MessageManager getMessageManager() { return messageManager; }
    public RankManager getRankManager() { return rankManager; }
    public Database getDatabase() { return database; }
    public DiscordWebhook getDiscordWebhook() { return discordWebhook; }
}
