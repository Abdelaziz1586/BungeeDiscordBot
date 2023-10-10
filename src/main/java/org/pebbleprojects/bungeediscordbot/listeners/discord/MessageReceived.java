package org.pebbleprojects.bungeediscordbot.listeners.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.pebbleprojects.bungeediscordbot.Handler;

import java.util.List;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) return;

        final Member member = event.getMember();

        if (member != null) {
            final String channelId = Handler.INSTANCE.getConfig().getString("news-channel", null);

            if (channelId == null || !event.getChannel().getId().equalsIgnoreCase(channelId)) return;

            final Role role = !member.getRoles().isEmpty() ? member.getRoles().get(0) : null;

            final String roleName = role != null ? (role.getColor() != null ? ChatColor.of(role.getColor()) + " " : "") + role.getName() : null;

            sendToAllPlayers(new TextComponent(ChatColor.translateAlternateColorCodes('&', Handler.INSTANCE.getConfig().getString("minecraft-chat-format", "[&bDiscord | %role%] %username% » %message%")).replace("%role%", roleName != null ? roleName : "").replace("%username%", member.getNickname() != null ? member.getNickname() : member.getEffectiveName()).replace("%message%", event.getMessage().getContentStripped())));
        }
    }

    private void sendToAllPlayers(final TextComponent message) {
        final List<String> allowedList = Handler.INSTANCE.getConfig().getStringList("enabled-servers");

        for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (allowedList.contains(player.getServer().getInfo().getName())) {
                player.sendMessage(message);
            }
        }
    }

}
