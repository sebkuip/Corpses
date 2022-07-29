package me.sebkuip.corpses;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Corpses extends JavaPlugin {
    private Ticker ticker = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pm = getServer().getPluginManager();
        this.saveDefaultConfig();

        pm.registerEvents(new DeathEventListener(this), this);
        pm.registerEvents(new CorpseClickEvent(this), this);
        ticker = new Ticker(this);

        getCommand("spawncorpse").setExecutor(new CreateCommand(this));
    }

    @Override
    public void onDisable() {
        ticker.cancel();
    }
}
