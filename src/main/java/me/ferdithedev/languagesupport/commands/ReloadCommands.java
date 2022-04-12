package me.ferdithedev.languagesupport.commands;

import me.ferdithedev.languagesupport.AlplayUtils;
import me.ferdithedev.languagesupport.LSLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(AlplayUtils.getLanguageSupport().isNotLoaded()) return false;
        if(sender.hasPermission("au.ls.reload")) {
            if(args.length > 0) {
                switch (args[0]) {
                    case "config" -> {
                        reloadConfig();
                        sender.sendMessage("reload-config-success");
                    }
                    case "players" -> {
                        AlplayUtils.getLanguageSupport().reloadPlayers();
                        sender.sendMessage("reload-config-success");
                    }
                    case "languages" -> {
                        AlplayUtils.getLanguageSupport().reloadLangConfigs();
                        sender.sendMessage("reload-config-success");
                    }
                    default -> {
                        sender.sendMessage("wrong-argument");
                    }
                }
            } else {
                reloadConfig();
                sender.sendMessage("reload-config-success");
            }
        } else
            sender.sendMessage("no-permission");
        return true;
    }

    private void reloadConfig() {
        AlplayUtils.getLanguageSupport().reloadConfig();
        AlplayUtils.getLanguageSupport().setLanguages((List<LSLanguage>) AlplayUtils.getLanguageSupport().getLSConfig().get("Languages"));
        AlplayUtils.getLanguageSupport().loadEnabledLanguages();
        AlplayUtils.getLanguageSupport().loadEnabledTranslations();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(sender.hasPermission("ls.reload")) {
            List<String> list = new ArrayList<>();
            if(args.length == 1) {
                list.add("config");
                list.add("players");
                list.add("languages");
            }
            return list;
        }
        return null;
    }
}
