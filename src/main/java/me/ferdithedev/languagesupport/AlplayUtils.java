package me.ferdithedev.languagesupport;

import me.ferdithedev.languagesupport.commands.LangCommand;
import me.ferdithedev.languagesupport.commands.ListCommand;
import me.ferdithedev.languagesupport.commands.ReloadCommands;
import me.ferdithedev.languagesupport.events.JoinEvent;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class AlplayUtils extends JavaPlugin {

    private static AlplayUtils instance;
    private static LanguageSupport languageSupport;

    static {
        ConfigurationSerialization.registerClass(LSLanguage.class, "LSLanguage");
    }

    @Override
    public void onEnable() {
        instance = this;

        registerCommands();
        languageSupport = new LanguageSupport(this);

        registerEvents();
    }


    public static LanguageSupport getLanguageSupport() {
        return languageSupport;
    }

    public static AlplayUtils getInstance() {
        return instance;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(languageSupport,this);
    }

    private void registerCommands() {
        getCommand("lslist").setExecutor(new ListCommand());

        getCommand("lsreload").setExecutor(new ReloadCommands());
        getCommand("lsreload").setTabCompleter(new ReloadCommands());

        getCommand("lang").setExecutor(new LangCommand());
        getCommand("lang").setTabCompleter(new LangCommand());
    }

}
