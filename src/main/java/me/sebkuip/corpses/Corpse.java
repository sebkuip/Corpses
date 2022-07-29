package me.sebkuip.corpses;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

public class Corpse extends ServerPlayer {
    private static HashMap<UUID, String[]> skinCache = new HashMap<>();

    public Corpse(MinecraftServer minecraftServer, ServerLevel worldServer, GameProfile gameProfile) {
        super(minecraftServer, worldServer, gameProfile, null);
    }

    public void setSkin(UUID uuid) {
        String[] skindata = getSkin(uuid);
        String signature = skindata[0];
        String texture = skindata[1];
        this.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        this.getEntityData().set(Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0xFF);
    }

    public void remove() {
        this.remove(RemovalReason.DISCARDED);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JsonObject readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return JsonParser.parseString(jsonText).getAsJsonObject();
        }
    }

    private String[] getSkin(UUID uuid) {
        String[] skin = skinCache.get(uuid);
        if (skin != null) {
            return skin;
        }

        try {
            JsonObject skindata = readJsonFromUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false");
            JsonObject properties = skindata.getAsJsonArray("properties").get(0).getAsJsonObject();
            String signature = properties.get("signature").getAsString();
            String texture = properties.get("value").getAsString();

            skin = new String[] {signature, texture};
            skinCache.put(uuid, skin);
            return skin;
        } catch (Throwable t) {
            t.printStackTrace();
            return new String[] {"", ""};
        }
    }
}
