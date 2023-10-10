package org.pebbleprojects.bungeediscordbot.utils;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DiscordWebhook {

    private String url;

    public void setUrl(final String url) {
        this.url = url;
    }

    public void execute(final String message, final String playerName, final EmbedObject embed, final boolean isSystemMessage) throws IOException {
        if (url == null || (message == null && embed == null)) return;

        final JSONObject json = new JSONObject();

        json.put("content", message);
        json.put("username", isSystemMessage ? "Bedwarsmc.io" : playerName);
        json.put("avatar_url", isSystemMessage ? "https://i.imgur.com/EaOY9yh.png" : "https://mc-heads.net/avatar/" + playerName);

        if (embed != null) {
            final JSONObject jsonEmbed = getJsonObject(embed);

            json.put("embeds", Collections.singletonList(jsonEmbed).toArray());
        }

        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "BedwarsMC");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        final OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes());
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }

    public void sendJoinEmbed(final String playerName) {
        try {
            execute(null, playerName, new EmbedObject()
                    .setColor(Color.GREEN)
                    .setAuthor(playerName + " joined the server", null, "https://mc-heads.net/avatar/" + playerName), true);
        } catch (final IOException ignored) {}
    }

    public void sendLeaveEmbed(final String playerName) {
        try {
            execute(null, playerName, new EmbedObject()
                    .setColor(Color.RED)
                    .setAuthor(playerName + " left the server", null, "https://mc-heads.net/avatar/" + playerName), true);
        } catch (final IOException ignored) {}
    }

    public void sendStartMessage() {
        try {
            execute(":white_check_mark: **Server has started**", null, null, true);
        } catch (final IOException ignored) {}
    }

    @NotNull
    private static JSONObject getJsonObject(EmbedObject embed) {
        final JSONObject jsonEmbed = new JSONObject();

        if (embed.getColor() != null) {
            final Color color = embed.getColor();

            int rgb = color.getRed();
            rgb = (rgb << 8) + color.getGreen();
            rgb = (rgb << 8) + color.getBlue();

            jsonEmbed.put("color", rgb);
        }

        final EmbedObject.Author author = embed.getAuthor();

        if (author != null) {
            final JSONObject jsonAuthor = new JSONObject();

            jsonAuthor.put("name", author.getName());
            jsonAuthor.put("url", author.getUrl());
            jsonAuthor.put("icon_url", author.getIconUrl());
            jsonEmbed.put("author", jsonAuthor);
        }
        return jsonEmbed;
    }


    public static class EmbedObject {
        private Color color;

        private Author author;

        public Color getColor() {
            return color;
        }

        public Author getAuthor() {
            return author;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }
    }

    private static class JSONObject {
        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final Set<Map.Entry<String, Object>> entrySet = map.entrySet();

            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }

}
