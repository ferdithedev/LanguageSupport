package me.ferdithedev.languagesupport.events;

import me.ferdithedev.languagesupport.AlplayUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(AlplayUtils.getLanguageSupport().isNotLoaded()) return;
        AlplayUtils.getLanguageSupport().initPlayerToLangConfig(event.getPlayer());
    }

}
