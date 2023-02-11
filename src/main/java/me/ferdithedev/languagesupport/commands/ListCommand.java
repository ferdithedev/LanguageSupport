package me.ferdithedev.languagesupport.commands;

import me.ferdithedev.languagesupport.Main;
import me.ferdithedev.languagesupport.LSLanguage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(Main.getLanguageSupport().isNotLoaded()) return false;
        if(sender instanceof Player) {
            sender.sendMessage("languages-list");
            for(LSLanguage lang : Main.getLanguageSupport().getLanguages()) {
                TextComponent message = new TextComponent(" -");
                BaseComponent language = new TextComponent(lang.name() + " ("+lang.code()+")");
                if(lang.enabled()) {
                    if(Main.getLanguageSupport().getPlayerLanguage((Player) sender).code().equals(lang.code())) {
                        language.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("DisabledColor")) + Main.getLanguageSupport().translate("language-already-set-list,vars={lang="+lang.name()+"}", lang)).create()));
                    } else {
                        language.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("EnabledColor")) + Main.getLanguageSupport().translate("click-to-set-language,vars={lang="+lang.name()+"}", lang) + "/" + Main.getLanguageSupport().translate("click-to-set-language,vars={lang="+lang.name()+"}", Main.getLanguageSupport().getPlayerLanguage((Player) sender))).create()));
                        language.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lang " + lang.code()));
                    }

                    language.setColor(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("EnabledColor")));
                } else {
                    if(Main.getLanguageSupport().getPlayerLanguage((Player) sender).code().equals(lang.code())) {
                        language.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("DisabledColor")) + Main.getLanguageSupport().translate("language-unavailable-list,vars={lang="+lang.name()+"}", lang)).create()));
                    } else {
                        language.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("DisabledColor")) + Main.getLanguageSupport().translate("language-unavailable-list,vars={lang="+lang.name()+"}", lang) + "/" + Main.getLanguageSupport().translate("language-unavailable-list,vars={lang="+lang.name()+"}", Main.getLanguageSupport().getPlayerLanguage((Player) sender))).create()));
                    }
                    language.setColor(ChatColor.valueOf(Main.getLanguageSupport().getLSConfig().getString("DisabledColor")));
                }

                message.addExtra(language);
                Player p = (Player) sender;
                p.spigot().sendMessage(message);
            }
        } else {
            sender.sendMessage(Main.getLanguageSupport().translate("languages-list", Main.getLanguageSupport().getDefaultLanguage()));
            for(LSLanguage lang : Main.getLanguageSupport().getLanguages()) {
                if(lang.enabled()) {
                    sender.sendMessage("Enabled: -"+lang.name() + " ("+lang.code()+")");
                } else {
                    sender.sendMessage("Disabled: -"+lang.name() + " ("+lang.code()+")");
                }
            }
        }
        return false;
    }
}
