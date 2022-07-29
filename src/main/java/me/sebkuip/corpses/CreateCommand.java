package me.sebkuip.corpses;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreateCommand implements CommandExecutor {
    private final Plugin plugin;

    public CreateCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (commandSender instanceof Player && command.getName().equalsIgnoreCase("spawncorpse")) {
            ServerPlayer sp = ((CraftPlayer) commandSender).getHandle();
            Location loc = ((CraftPlayer) commandSender).getLocation();
            MinecraftServer server = sp.getServer();
            ServerLevel level = sp.getLevel();
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ((CraftPlayer) commandSender).getDisplayName() + "'s corpse");

            Corpse corpse = new Corpse(server, level, gameProfile);
            corpse.setSkin(sp.getUUID());

            corpse.setPos(loc.getX(), loc.getY(), loc.getZ());
            corpse.setXRot(sp.getXRot());
            corpse.setYRot(sp.getYRot());

            commandSender.getServer().getOnlinePlayers().forEach(player -> {
                ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;
                ps.send(new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, corpse));
                ps.send(new ClientboundAddPlayerPacket(corpse));
            });
            return true;
        }
        return false;
    }
}
