package org.pebbleprojects.bungeediscordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.pebbleprojects.bungeediscordbot.listeners.Chat;
import org.pebbleprojects.bungeediscordbot.listeners.PostLogin;
import org.pebbleprojects.bungeediscordbot.listeners.PlayerDisconnect;
import org.pebbleprojects.bungeediscordbot.listeners.discord.MessageReceived;
import org.pebbleprojects.bungeediscordbot.utils.DiscordWebhook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class Handler {

    private JDA jda;
    private final Logger logger;
    private Configuration config;
    private final File configFile;
    public static Handler INSTANCE;
    private final DiscordWebhook webhook;

    public Handler() {
        INSTANCE = this;

        logger = BungeeDiscordBot.INSTANCE.getLogger();

        if (!BungeeDiscordBot.INSTANCE.getDataFolder().mkdir()) logger.info("Created plugin folder!");

        configFile = new File(BungeeDiscordBot.INSTANCE.getDataFolder().getPath(), "config.yml");
        if (!configFile.exists()) {
            try {
                Files.copy(BungeeDiscordBot.INSTANCE.getResourceAsStream("config.yml"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        webhook = new DiscordWebhook();

        update();

        webhook.sendStartMessage();

        final PluginManager pm = BungeeDiscordBot.INSTANCE.getProxy().getPluginManager();

        pm.registerListener(BungeeDiscordBot.INSTANCE, new Chat());
        pm.registerListener(BungeeDiscordBot.INSTANCE, new PostLogin());
        pm.registerListener(BungeeDiscordBot.INSTANCE, new PlayerDisconnect());

        pm.registerCommand(BungeeDiscordBot.INSTANCE, new Command());
    }

    public void update() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            webhook.setUrl(config.getString("discord-webhook", null));

            new Thread(() -> {
                final String token = config.getString("bot-token", null);

                if (token == null) {
                    logger.warning("Bot token is invalid!");
                    if (jda != null) {
                        jda.shutdown();
                        jda = null;
                    }
                    return;
                }

                if (jda != null) jda.shutdown();

                try {
                    jda = JDABuilder.createDefault(token)
                            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                            .setBulkDeleteSplittingEnabled(false)
                            .build();

                    jda.addEventListener(new MessageReceived());
                } catch (final InvalidTokenException ignored) {
                    logger.severe("Bot token is invalid!");
                    jda = null;
                }
            }).start();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final Configuration getConfig() {
        return config;
    }

    public final DiscordWebhook getWebhook() {
        return webhook;
    }
}
