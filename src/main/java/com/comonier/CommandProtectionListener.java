package com.comonier;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.List;

public class CommandProtectionListener implements Listener {

    private final List<String> protectedCommands = Arrays.asList("rank", "rankup", "rankreload");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();
        if (protectedCommands.contains(cmd)) {
            if (!event.getMessage().startsWith("/rankup:") && !event.getMessage().startsWith("/rank:")) {
                String args = event.getMessage().contains(" ") ? event.getMessage().substring(event.getMessage().indexOf(" ")) : "";
                event.setMessage("/rankup:" + cmd + args);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsoleCommand(ServerCommandEvent event) {
        String cmd = event.getCommand().split(" ")[0].toLowerCase();
        if (protectedCommands.contains(cmd)) {
            if (!event.getCommand().startsWith("rankup:")) {
                event.setCommand("rankup:" + event.getCommand());
            }
        }
    }
}
