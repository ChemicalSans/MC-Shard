package net.nussi.mcs;

import net.nussi.mcs.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftShardPlugin extends JavaPlugin {
    net.nussi.mcs.player.PlayerHandler PlayerHandler = new PlayerHandler();

    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new EventHandler(), this);
        Bukkit.broadcastMessage("MinecraftShardPlugin Enabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Enabled!");


        getServer().getPluginManager().registerEvents(PlayerHandler, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, PlayerHandler, 0, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerHandler.onPlayerQuitEvent(new PlayerQuitEvent(player, "The Server is Closing!"));
            player.kickPlayer("The Server is Closing!");
        });

        Bukkit.broadcastMessage("MinecraftShardPlugin Disabled!");
        Bukkit.getLogger().info("MinecraftShardPlugin Disabled!");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("shard")) {
            PlayerHandler.SERVERID = args[0];
            sender.sendMessage("Shard updated to: " + PlayerHandler.SERVERID);
        }

        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;



        return false;
    }
}
