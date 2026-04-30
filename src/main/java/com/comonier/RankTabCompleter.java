package com.comonier;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RankTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("rankup")) {
            // /rankup não possui sub-argumentos necessários
            return Collections.emptyList();
        }

        if (command.getName().equalsIgnoreCase("rank")) {
            if (args.length == 1) {
                // Sugestões para sub-comandos futuros ou apenas visualização
                List<String> subCommands = Arrays.asList("info", "menu");
                StringUtil.copyPartialMatches(args[0], subCommands, completions);
                Collections.sort(completions);
            }
        }

        if (command.getName().equalsIgnoreCase("rankreload")) {
            // Apenas sugere se tiver permissão
            if (sender.hasPermission("rankup.admin")) {
                return Collections.emptyList();
            }
        }

        return completions;
    }
}
