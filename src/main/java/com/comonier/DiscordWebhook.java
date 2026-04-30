package com.comonier;

import org.json.simple.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class DiscordWebhook {

    private final Rankup plugin;

    public DiscordWebhook(Rankup plugin) {
        this.plugin = plugin;
    }

    public void sendRankupMessage(String playerName, String rankName) {
        if (!plugin.getConfig().getBoolean("discord-webhook.enabled")) return;

        String webhookUrl = plugin.getConfig().getString("discord-webhook.url");
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("COLE_SEU_WEBHOOK_AQUI")) {
            return;
        }

        // Rodar de forma assíncrona para não causar lag no servidor
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "Java-Discord-Webhook");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                String title = plugin.getMessageManager().getMessage("discord.rankup-title");
                String description = plugin.getMessageManager().getMessage("discord.rankup-description")
                        .replace("%player%", playerName)
                        .replace("%rank_name%", rankName);

                // JSON simples para o Webhook
                String json = "{"
                        + "\"username\": \"" + plugin.getConfig().getString("discord-webhook.username") + "\","
                        + "\"avatar_url\": \"" + plugin.getConfig().getString("discord-webhook.avatar-url") + "\","
                        + "\"embeds\": [{"
                        + "  \"title\": \"" + title + "\","
                        + "  \"description\": \"" + description + "\","
                        + "  \"color\": 65280" // Verde
                        + "}]"
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(json.getBytes("UTF-8"));
                }

                connection.getInputStream().close();
                connection.disconnect();

            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Erro ao enviar webhook para o Discord!", e);
            }
        });
    }
}
