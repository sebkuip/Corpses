package me.sebkuip.corpses;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.util.Date;

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

            LivingEntity corpse = (LivingEntity) player.getWorld().spawnEntity(loc, EntityType.ZOMBIE, false);
            corpse.setCustomName(ChatColor.YELLOW + player.getDisplayName() + "'s corpse");
            corpse.setInvulnerable(true);
            corpse.setSilent(true);
            corpse.setPersistent(true);
            corpse.setAI(false);
            corpse.setCanPickupItems(false);
            corpse.setArrowsInBody(player.getArrowsInBody());

            EntityEquipment equipment = player.getEquipment();
            EntityEquipment corpseEquipment = corpse.getEquipment();
            boolean showEquipment = plugin.getConfig().getBoolean("show-equipment");
            ItemStack helmet = equipment.getHelmet();
            if (helmet != null && showEquipment) {
                corpseEquipment.setHelmet(helmet);
            } else {
                corpseEquipment.setHelmet(new ItemStack(Material.SKELETON_SKULL), false);
            }
            ItemStack chestplate = equipment.getChestplate();
            if (chestplate != null && showEquipment) {
                corpseEquipment.setChestplate(chestplate);
            }
            ItemStack leggings = equipment.getLeggings();
            if (leggings != null && showEquipment) {
                corpseEquipment.setLeggings(leggings);
            }
            ItemStack boots = equipment.getBoots();
            if (boots != null && showEquipment) {
                corpseEquipment.setBoots(boots);
            }
            ItemStack wielding = equipment.getItemInMainHand();
            if (showEquipment) {
                corpseEquipment.setItemInMainHand(wielding);
            }

            PersistentDataContainer pdc = corpse.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "owner"), DataType.UUID, player.getUniqueId());
            pdc.set(new NamespacedKey(plugin, "inventory"), DataType.ITEM_STACK_ARRAY, player.getInventory().getContents());
            pdc.set(new NamespacedKey(plugin, "exp"), DataType.INTEGER, Math.round(player.getExp()));
            pdc.set(new NamespacedKey(plugin, "deathtime"), DataType.DATE, new Date());

            event.getDrops().clear();
            event.setDroppedExp(0);
            player.setTotalExperience(0);
        }
    }
}
