package me.sebkuip.corpses;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Ticker {
    private final Plugin plugin;
    private BukkitTask tickTask;

    public Ticker(Plugin plugin) {
        this.plugin = plugin;
        tick();
    }

    private void tick() {
        plugin.getLogger().info("Starting ticker task");
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(World world: plugin.getServer().getWorlds()) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    PersistentDataContainer pdc = entity.getPersistentDataContainer();
                    if (entity.getType() == EntityType.ZOMBIE && pdc.has(new NamespacedKey(plugin, "owner"), DataType.UUID)) {
                        int despawntime = plugin.getConfig().getInt("despawn-timer");
                        long deathTime = TimeUnit.MINUTES.convert(new Date().getTime() - pdc.getOrDefault(new NamespacedKey(plugin, "deathtime"), DataType.DATE, new Date(0)).getTime(), TimeUnit.MILLISECONDS);
                        if (despawntime != -1 && deathTime > despawntime) {
                            UUID uuid = pdc.getOrDefault(new NamespacedKey(plugin, "owner"), DataType.UUID, new UUID(0, 0));
                            Player player = plugin.getServer().getPlayer(uuid);
                            if (player != null && plugin.getConfig().getBoolean("announce-rot")) {
                                player.sendRawMessage(ChatColor.RED + "Your corpse rotted away...");
                            }
                            entity.remove();
                        }
                    }
                }
            }
        }, 20, 20);
    }

    public void cancel() {
        if (!tickTask.isCancelled()) {
            plugin.getLogger().info("Stopping ticker task");
            tickTask.cancel();
        }
    }
}
