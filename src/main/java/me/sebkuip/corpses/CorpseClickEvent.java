package me.sebkuip.corpses;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import java.util.UUID;

public class CorpseClickEvent implements Listener {
    private final Plugin plugin;

    public CorpseClickEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    private void inventoryTransfer(PlayerInventory player, ItemStack[] stored) {
        System.out.println("Transferring inventory");
        for (int i = 0; i < stored.length; i++) {
            if (stored[i] == null) {
                continue;
            }
            if (player.getItem(i) == null) {
                player.setItem(i, stored[i]);
            } else {
                player.getHolder().getWorld().dropItem(player.getHolder().getLocation(), stored[i]);
            }
        }
        System.out.println("Done transferring");
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Entity e = event.getRightClicked();
        Player player = event.getPlayer();
        if (e instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e;
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            UUID uuid = pdc.getOrDefault(new NamespacedKey(plugin, "owner"), DataType.UUID, new UUID(0, 0));
            if (entity.getType() == EntityType.ZOMBIE && uuid.equals(player.getUniqueId())) {
                player.sendRawMessage(ChatColor.RED + "You removed this corpse");
                player.giveExp(pdc.getOrDefault(new NamespacedKey(plugin, "exp"), DataType.INTEGER, 0));
                inventoryTransfer(player.getInventory(), pdc.get(new NamespacedKey(plugin, "inventory"), DataType.ITEM_STACK_ARRAY));
                entity.remove();
            }
        }
    }
}
