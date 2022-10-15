package net.nussi.mcs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class NPC {

    private static List<NPC> NPCS = new ArrayList<>();

    EntityPlayer npc;
    GameProfile gameProfile;
    String name;
    UUID uuid;

    public NPC(String name, Location location) {
        this.name = name;
        uuid = UUID.randomUUID();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
        gameProfile = new GameProfile(uuid, name);
        try {
            String uuid = NPC.getUUID(name);
            String[] textureData = NPC.getSkin(uuid);

            gameProfile.getProperties().put("textures", new Property("textures", textureData[0], textureData[1]));
        } catch (NpcNotFoundException e) {
            Bukkit.getLogger().info("Skin for " + name + " was not found! Debug: " + e.getMessage());
        }

        npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));

        npc.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        addNPCPacket();

    }

    public void addNPCPacket() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));
        }
    }

    public void addNPCPacket(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));
    }

    public void removeNPCPacket() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));
//            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY));
        }
    }

    public void teleport(double x, double y, double z, float yaw, float pitch) {
        npc.setLocation(x, y, z, yaw, pitch);
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityTeleport(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));
        }
    }

//    public void lookAt(Location location) {
//        //TODO GET LIFE
//        Vec3D vec = new Vec3D(
//                npc.locX()- location.getX(),
//                npc.locY()- location.getY(),
//                npc.locZ()- location.getZ()
//        );
//
//        double hyp = Math.sqrt(vec.x*vec.x + vec.y*vec.y + vec.z*vec.z);
//        vec = new Vec3D(
//                vec.x/hyp,
//                vec.y/hyp,
//                vec.z/hyp
//        );
//
//        float pitch = (float) Math.asin(-vec.y) * 256/360;
//        float yaw = (float) Math.toDegrees(Math.atan2(vec.x, vec.z));
//
//        teleport(
//                npc.locX(),
//                npc.locY(),
//                npc.locZ(),
//                yaw,
//                pitch
//        );
//    }


    public static List<NPC> getNPCS() {
        return NPCS;
    }

    public static void createNPCS(String name, Location location) {
        NPC npc = new NPC(name, location);
        NPCS.add(npc);
    }

    public static NPC getNPC(String name) throws NpcNotFoundException {
        for(NPC npc : NPCS) {
            if(npc.name.equals(name)) {
                return npc;
            }
        }
        throw new NpcNotFoundException("NPC " + name + " was not found!");
    }

    public static String getUUID(String playerName) throws NpcNotFoundException {
        try {
            Unirest.config().verifySsl(false);
            HttpResponse<JsonNode> response = Unirest.get("https://api.mojang.com/users/profiles/minecraft/"+playerName).asJson();
            if(!response.isSuccess()) throw new NpcNotFoundException(response.getStatusText());
            String uuid = response.getBody().getObject().getString("id");
            return uuid;
        } catch (Exception e) {
            throw new NpcNotFoundException(e.getMessage());
        }
    }

    public static String[] getSkin(String uuid) throws NpcNotFoundException {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").asJson();
            if(!response.isSuccess()) throw new NpcNotFoundException(response.getStatusText());
            JSONObject properties = response.getBody().getObject().getJSONArray("properties").getJSONObject(0);

            String texture = properties.getString("value");
            String signature = properties.getString("signature");
            return new String[]{texture, signature};
        } catch (Exception e) {
            throw new NpcNotFoundException(e.getMessage());
        }
    }

    static class NpcNotFoundException extends Exception {
        public NpcNotFoundException(String message) {
            super(message);
        }
    }

}
