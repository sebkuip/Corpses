package me.sebkuip.corpses;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.naming.Name;

public class DeathEventListener implements Listener {
    private final Plugin plugin;

    public DeathEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!event.getKeepInventory()) {
            Player player = event.getEntity();
            Location loc = player.getLocation();

            LivingEntity corpse = (LivingEntity) player.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
            corpse.setCustomName(ChatColor.YELLOW + player.getDisplayName() + "'s corpse");
            corpse.setInvulnerable(true);
            corpse.setSilent(true);
            corpse.setPersistent(true);
            corpse.setAI(false);
            corpse.setArrowsInBody(player.getArrowsInBody());
            corpse.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));

            PersistentDataContainer pdc = corpse.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "owner"), DataType.UUID, player.getUniqueId());
            pdc.set(new NamespacedKey(plugin, "inventory"), DataType.ITEM_STACK_ARRAY, player.getInventory().getContents());
            pdc.set(new NamespacedKey(plugin, "exp"), DataType.INTEGER, Math.round(player.getExp()));

            event.getDrops().clear();
            event.setDroppedExp(0);
            player.setTotalExperience(0);
        }
    }
}
