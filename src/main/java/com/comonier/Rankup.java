package com.comonier;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Rankup extends JavaPlugin {

    private static Rankup instance;
    private static Economy econ = null;
    
    private MessageManager messageManager;
    private RankManager rankManager;
    private Database database;
    private DiscordWebhook discordWebhook;
    private NumberFormat moneyFormatter;

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
        setupMoneyFormatter();
        
        this.messageManager = new MessageManager(this);
        this.rankManager = new RankManager(this);
        this.database = new Database(this);
        this.discordWebhook = new DiscordWebhook(this);

        RankTabCompleter tabCompleter = new RankTabCompleter();
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("rankup").setExecutor(new RankupCommand(this));
        
        getCommand("rankreload").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("rankup.admin")) {
                sender.sendMessage(messageManager.getMessage("no-permission"));
                return true;
            }
            reloadConfig();
            setupMoneyFormatter(); // Recarrega o formato caso mude no config
            messageManager.loadMessages();
            rankManager.loadRanks();
            sender.sendMessage(messageManager.getMessage("plugin-reloaded"));
            return true;
        });

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new RankListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RankupExpansion(this).register();
        }
    }

    private void setupMoneyFormatter() {
        String localeTag = getConfig().getString("settings.money-format-locale", "pt-BR");
        try {
            Locale locale = Locale.forLanguageTag(localeTag);
            this.moneyFormatter = NumberFormat.getCurrencyInstance(locale);
        } catch (Exception e) {
            this.moneyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            getLogger().warning("Locale invalido no config. Usando pt-BR por padrao.");
        }
    }

    public String formatMoney(double amount) {
        // Formata e remove o simbolo da moeda (R$, $, etc) para manter o chat limpo
        return moneyFormatter.format(amount)
                .replaceAll("[^0-9,.]", " ")
                .trim()
                .replace(" ,", ",") // Limpeza de espaços residuais
                .replace(" .", ".");
    }

    @Override
    public void onDisable() {
        if (database != null) database.close();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void createCustomFiles() {
        List<String> files = Arrays.asList("messages_pt.yml", "messages_en.yml", "messages_es.yml", "messages_ru.yml", "ranks.yml");
        for (String fileName : files) {
            File file = new File(getDataFolder(), fileName);
            if (!file.exists()) saveResource(fileName, false);
        }
    }

    public static Rankup getInstance() { return instance; }
    public static Economy getEconomy() { return econ; }
    public MessageManager getMessageManager() { return messageManager; }
    public RankManager getRankManager() { return rankManager; }
    public Database getDatabase() { return database; }
    public DiscordWebhook getDiscordWebhook() { return discordWebhook; }
}
