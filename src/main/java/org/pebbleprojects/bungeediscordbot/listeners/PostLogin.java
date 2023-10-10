package org.pebbleprojects.bungeediscordbot.listeners;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.pebbleprojects.bungeediscordbot.Handler;

public class PostLogin implements Listener {

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        new Thread(() -> Handler.INSTANCE.getWebhook().sendJoinEmbed(event.getPlayer().getName())).start();
    }

}
