package net.nussi.mcs;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome!");
        if(NPC.getNPCS() == null) return;
        if(NPC.getNPCS().isEmpty()) return;
        NPC.getNPCS().forEach(npc -> {
            npc.addJoinPacket(event.getPlayer());
        });

    }


}
