package net.nussi.mcs.player;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import net.nussi.mcs.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PlayerHandler implements Runnable, Listener {
    public static String SERVERID = "alpha";

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Location location = player.getLocation();

        Unirest.post("http://localhost:8080/player")
                .header("mcs_player_uuid", player.getUniqueId().toString())
                .header("mcs_player_name", player.getName())
                .header("mcs_player_server", SERVERID)
                .header("mcs_player_world", location.getWorld().getName())
                .header("mcs_player_x", String.valueOf( location.getX()))
                .header("mcs_player_y", String.valueOf( location.getY()))
                .header("mcs_player_z", String.valueOf( location.getZ()))
                .header("mcs_player_yaw", String.valueOf( location.getYaw()))
                .header("mcs_player_pitch", String.valueOf( location.getPitch()))
                .asJson();
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Unirest.delete("http://localhost:8080/player/"+ player.getUniqueId()).asJson();
    }

    @EventHandler
    public void  onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        Unirest.put("http://localhost:8080/player/"+ player.getUniqueId())
                .header("mcs_player_server", SERVERID)
                .header("mcs_player_world", location.getWorld().getName())
                .header("mcs_player_x", String.valueOf( location.getX()))
                .header("mcs_player_y", String.valueOf( location.getY()))
                .header("mcs_player_z", String.valueOf( location.getZ()))
                .header("mcs_player_yaw", String.valueOf( location.getYaw()))
                .header("mcs_player_pitch", String.valueOf( location.getPitch()))
                .asJson();
    }


    private ArrayList<PlayerModel> oldModels = new ArrayList<>();

    @Override
    public void run() {
        try {
            ArrayList<PlayerModel> currentModels = getPlayerModels();
            if(currentModels != oldModels) {
                oldModels = currentModels;
                updateNPCS(currentModels);
            }
        } catch (PlayerModelNotFoundException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }

    }

    public void updateNPCS(ArrayList<PlayerModel> playerModels) {
        ArrayList<String> playerUUIDs = getOnlinePlayersUUID();
        HashMap<String, PlayerModel> currentModels = new HashMap<>();
        Stack<PlayerModel> modelStack = new Stack<>();

        for(PlayerModel model : playerModels) {
            if(model.getServer().equals(SERVERID)) continue;
            if(playerUUIDs.contains(model.getUuid())) continue;
            currentModels.put(model.getName(), model);
            modelStack.add(model);
        }

        for(int i = 0; i < NPC.getNPCS().size(); i++) {
                NPC npc = NPC.getNPCS().get(i);
                PlayerModel model = currentModels.get(npc.getName());
                modelStack.remove(model);
                if(model == null) {
                    try {
                        NPC.deleteNPC(npc.getName());
                    } catch (Exception e) {
                        Bukkit.getLogger().warning(e.getMessage());
                    }
                } else {
                    npc.teleport(
                            model.getX(),
                            model.getY(),
                            model.getZ(),
                            model.getYaw(),
                            model.getPitch()
                    );
                }
        }

        while (!modelStack.isEmpty()) {
            PlayerModel model = modelStack.pop();
            if(model.getServer().equals(SERVERID)) continue;
            NPC.createNPCS(model.getName(), new Location(
                    Bukkit.getWorld(model.getWorld()),
                    model.getX(),
                    model.getY(),
                    model.getZ()
            ));
        }

    }

    private ArrayList<String> getOnlinePlayersUUID() {
        ArrayList<String> ret = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            ret.add(player.getUniqueId().toString());
        });
        return ret;
    }

    public static ArrayList<PlayerModel> getPlayerModels() throws PlayerModelNotFoundException {
        ArrayList<PlayerModel> models = new ArrayList<>();

        Unirest.config().verifySsl(false);
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:8080/player").asJson();

        if(!response.isSuccess()) throw new PlayerModelNotFoundException();

        JSONArray data = response.getBody().getArray();

        for(int i = 0; i < data.length(); i++) {
            JSONObject playerModelData = data.getJSONObject(i);
            try {

                PlayerModel playerModel = new PlayerModel(
                        playerModelData.getInt("id"),
                        playerModelData.getString("name"),
                        playerModelData.getDouble("x"),
                        playerModelData.getDouble("y"),
                        playerModelData.getDouble("z"),
                        playerModelData.getString("world"),
                        playerModelData.getString("uuid"),
                        playerModelData.getString("server"),
                        playerModelData.getDouble("yaw"),
                        playerModelData.getDouble("pitch")
                );

                models.add(playerModel);
            } catch (JSONException e) {
                throw new PlayerModelNotFoundException(e.getMessage());
            }
        }

        return models;
    }


    static class PlayerModelNotFoundException extends Exception {
        public PlayerModelNotFoundException() {
            super();
        }
        public PlayerModelNotFoundException(String message) {
            super(message);
        }
    }

}
