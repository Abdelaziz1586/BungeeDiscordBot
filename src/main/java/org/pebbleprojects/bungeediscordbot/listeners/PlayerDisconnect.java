package org.pebbleprojects.bungeediscordbot.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.pebbleprojects.bungeediscordbot.Handler;

public class PlayerDisconnect implements Listener {

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        new Thread(() -> Handler.INSTANCE.getWebhook().sendLeaveEmbed(event.getPlayer().getName())).start();
    }

}
