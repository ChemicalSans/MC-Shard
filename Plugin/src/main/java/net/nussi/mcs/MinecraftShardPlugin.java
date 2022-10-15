package net.nussi.mcs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        Player player = (Player) sender;
        switch (label) {
            case "createnpc":
                if(args.length == 0) return true;
                String name = args[0];
                NPC.createNPCS(name, player.getLocation());
                player.sendMessage("Created NPC!");
                return false;
            case "updatenpc":
                if(args.length == 0) return true;
                try {
                    NPC npc = NPC.getNPC(args[0]);
                    npc.teleport(
                            player.getLocation().getX(),
                            player.getLocation().getY(),
                            player.getLocation().getZ(),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch()
                    );
//                    npc.lookAt(player.getLocation());
                } catch (NPC.NpcNotFoundException e) {
                    player.sendMessage("NPC " + args[0] + " was not found!");
                }
                return false;
        }

        return false;
    }
}
