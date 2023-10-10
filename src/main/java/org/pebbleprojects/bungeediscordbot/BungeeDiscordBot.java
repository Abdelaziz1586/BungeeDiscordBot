package org.pebbleprojects.bungeediscordbot;

import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeDiscordBot extends Plugin {

    public static BungeeDiscordBot INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        new Thread(Handler::new).start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
