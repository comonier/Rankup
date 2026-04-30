package com.comonier;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final Rankup plugin;
    private FileConfiguration messages;
    private final Map<String, String> cache = new HashMap<>();

    public MessageManager(Rankup plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        String lang = plugin.getConfig().getString("language", "pt");
        File file = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        
        if (!file.exists()) {
            plugin.saveResource("messages_pt.yml", false);
            file = new File(plugin.getDataFolder(), "messages_pt.yml");
        }

        messages = YamlConfiguration.loadConfiguration(file);
        cache.clear();
    }

    public String getMessage(String path) {
        if (cache.containsKey(path)) return cache.get(path);
        
        String msg = messages.getString(path);
        if (msg == null) return "§c[Erro: Mensagem '" + path + "' nao encontrada]";
        
        msg = format(msg);
        cache.put(path, msg);
        return msg;
    }

    public void sendMessage(Player player, String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        String prefix = getMessage("prefix");
        
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                // Proteção contra valores nulos nos placeholders
                String value = entry.getValue() != null ? entry.getValue() : "";
                message = message.replace(entry.getKey(), format(value));
            }
        }
        
        player.sendMessage(prefix + message);
    }

    public void sendMessage(Player player, String path) {
        sendMessage(player, path, null);
    }

    // Método blindado contra NullPointerException
    public String format(String text) {
        if (text == null || text.isEmpty()) {
            return ""; 
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
