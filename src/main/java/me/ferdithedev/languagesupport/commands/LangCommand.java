package me.ferdithedev.languagesupport.commands;

import me.ferdithedev.languagesupport.Main;
import me.ferdithedev.languagesupport.LSLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LangCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(Main.getLanguageSupport().isNotLoaded()) return false;
        if(sender.hasPermission("au.ls.setlang") || !Main.getLanguageSupport().getLSConfig().getBoolean("UseTheSetlangPermission")) {
            if(sender instanceof Player p) {
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("auto") && p.getLocale().contains("_")) {
                        String locale = p.getLocale().split("_")[0];
                        for(LSLanguage language : Main.getLanguageSupport().getLanguages()) {
                            if(language.code().equalsIgnoreCase(locale)) {
                                if(!Main.getLanguageSupport().getPlayerLanguage(p).code().equalsIgnoreCase(language.code())) {
                                    Main.getLanguageSupport().setPlayerLanguage(p,language);
                                    p.sendMessage("set-language-success,vars={lang="+language.name()+"}");
                                } else {
                                    p.sendMessage("language-already-set-chat,vars={lang="+language.name()+"}");
                                }
                                return true;
                            }
                        }
                        p.sendMessage("language-unavailable-chat,vars={lang="+locale+"}");
                    } else {
                        for(LSLanguage language : Main.getLanguageSupport().getLanguages()) {
                            if(args[0].equalsIgnoreCase(language.code()) || args[0].equalsIgnoreCase(language.name())) {
                                if(Main.getLanguageSupport().getEnabledLanguages().contains(language)) {
                                    if(!language.code().equals(Main.getLanguageSupport().getPlayerLanguage(p).code())) {
                                        Main.getLanguageSupport().setPlayerLanguage(p, language);
                                        sender.sendMessage("set-language-success,vars={lang="+language.name()+"}");
                                    } else {
                                        sender.sendMessage("language-already-set-chat,vars={lang="+language.name()+"}");
                                    }
                                } else {
                                    sender.sendMessage("language-unavailable-chat,vars={lang="+language.name()+"}");
                                }

                                return true;
                            }

                        }
                        sender.sendMessage("language-not-found,vars={lang=" + args[0] + "}");
                    }

                } else
                    sender.sendMessage("say-player-language,vars={lang=" + Main.getLanguageSupport().getPlayerLanguage((Player) sender).name() +"}");
            } else
                sender.sendMessage(Main.getLanguageSupport().translate("must-be-player", Main.getLanguageSupport().getDefaultLanguage()));
        } else
            sender.sendMessage("no-permission");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(sender.hasPermission("ls.setlang") || !Main.getLanguageSupport().getLSConfig().getBoolean("UseTheSetlangPermission")) {
            List<String> list = new ArrayList<>();
            if(args.length == 1) {
                Main.getLanguageSupport().getEnabledLanguages().forEach(language -> list.add(language.code()));
            }
            list.add("auto");
            return list;
        }
        return null;
    }

}
