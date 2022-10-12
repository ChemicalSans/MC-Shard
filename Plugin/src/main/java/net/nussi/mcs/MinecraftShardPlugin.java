package net.nussi.mcs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftShardPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new EventHandler(), this);
        Bukkit.broadcastMessage("MinecraftShardPlugin Enabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage("MinecraftShardPlugin Disabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Disabled!");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        switch (label) {
            case "createnpc":
                Player player = (Player) sender;
                NPC.createNPC(player);
                player.sendMessage("Created NPC!");
                return false;
            case "updatenpc":
                NPC.getNPCS().forEach(npc -> {
                    npc.relativeTeleport(0,1,0);
                });
                return false;
        }

        return false;
    }
}
