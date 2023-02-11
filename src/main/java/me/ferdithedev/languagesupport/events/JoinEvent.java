package me.ferdithedev.languagesupport.events;

import me.ferdithedev.languagesupport.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(Main.getLanguageSupport().isNotLoaded()) return;
        Main.getLanguageSupport().initPlayerToLangConfig(event.getPlayer());
    }

}
