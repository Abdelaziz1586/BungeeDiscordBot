package org.pebbleprojects.bungeediscordbot;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class Command extends net.md_5.bungee.api.plugin.Command {

    public Command() {
        super("bdb");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (sender.hasPermission("bdb.reload")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                Handler.INSTANCE.update();

                sender.sendMessage(new TextComponent("§aSuccessfully reloaded"));
                return;
            }

            sender.sendMessage(new TextComponent("§cInvalid arguments (reload)"));
        }
    }
}
