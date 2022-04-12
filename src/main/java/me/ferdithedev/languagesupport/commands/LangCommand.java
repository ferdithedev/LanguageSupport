package me.ferdithedev.languagesupport.commands;

import me.ferdithedev.languagesupport.AlplayUtils;
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
        if(AlplayUtils.getLanguageSupport().isNotLoaded()) return false;
        if(sender.hasPermission("au.ls.setlang") || !AlplayUtils.getLanguageSupport().getLSConfig().getBoolean("UseTheSetlangPermission")) {
            if(sender instanceof Player p) {
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("auto") && p.getLocale().contains("_")) {
                        String locale = p.getLocale().split("_")[0];
                        for(LSLanguage language : AlplayUtils.getLanguageSupport().getLanguages()) {
                            if(language.code().equalsIgnoreCase(locale)) {
                                if(!AlplayUtils.getLanguageSupport().getPlayerLanguage(p).code().equalsIgnoreCase(language.code())) {
                                    AlplayUtils.getLanguageSupport().setPlayerLanguage(p,language);
                                    p.sendMessage("set-language-success,vars={lang="+language.name()+"}");
                                } else {
                                    p.sendMessage("language-already-set-chat,vars={lang="+language.name()+"}");
                                }
                                return true;
                            }
                        }
                        p.sendMessage("language-unavailable-chat,vars={lang="+locale+"}");
                    } else {
                        for(LSLanguage language : AlplayUtils.getLanguageSupport().getLanguages()) {
                            if(args[0].equalsIgnoreCase(language.code()) || args[0].equalsIgnoreCase(language.name())) {
                                if(AlplayUtils.getLanguageSupport().getEnabledLanguages().contains(language)) {
                                    if(!language.code().equals(AlplayUtils.getLanguageSupport().getPlayerLanguage(p).code())) {
                                        AlplayUtils.getLanguageSupport().setPlayerLanguage(p, language);
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
                    sender.sendMessage("say-player-language,vars={lang=" + AlplayUtils.getLanguageSupport().getPlayerLanguage((Player) sender).name() +"}");
            } else
                sender.sendMessage(AlplayUtils.getLanguageSupport().translate("must-be-player",AlplayUtils.getLanguageSupport().getDefaultLanguage()));
        } else
            sender.sendMessage("no-permission");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(sender.hasPermission("ls.setlang") || !AlplayUtils.getLanguageSupport().getLSConfig().getBoolean("UseTheSetlangPermission")) {
            List<String> list = new ArrayList<>();
            if(args.length == 1) {
                AlplayUtils.getLanguageSupport().getEnabledLanguages().forEach(language -> list.add(language.code()));
            }
            list.add("auto");
            return list;
        }
        return null;
    }

}
