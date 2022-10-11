package net.nussi.mcs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftShardPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.broadcastMessage("MinecraftShardPlugin Enabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage("MinecraftShardPlugin Disabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Disabled!");
    }
}
