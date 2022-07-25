package me.sebkuip.corpses;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Corpses extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new DeathEventListener(this), this);
        pm.registerEvents(new CorpseClickEvent(this), this);
    }
}
