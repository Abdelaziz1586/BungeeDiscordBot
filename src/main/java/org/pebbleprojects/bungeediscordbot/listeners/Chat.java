package org.pebbleprojects.bungeediscordbot.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.pebbleprojects.bungeediscordbot.Handler;

import java.io.IOException;

public class Chat implements Listener {

    @EventHandler
    public void onChat(final ChatEvent event) {
        new Thread(() -> {
            if (event.isCommand()) return;

            try {
                Handler.INSTANCE.getWebhook().execute(event.getMessage(), ((ProxiedPlayer) event.getSender()).getName(), null, false);
            } catch (final IOException ignored) {}
        }).start();
    }

}
