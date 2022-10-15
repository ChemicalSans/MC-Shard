package net.nussi.mcs.shard.rest.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
@RequestMapping("/player")
public class PlayerController {
    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);
    private final PlayerRepository repository;

    public PlayerController(PlayerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity get() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity post(
            @RequestHeader("mcs_player_uuid") String uuid,
            @RequestHeader("mcs_player_name") String name,
            @RequestHeader("mcs_player_server") String server,
            @RequestHeader("mcs_player_world") String world,
            @RequestHeader("mcs_player_x") double x,
            @RequestHeader("mcs_player_y") double y,
            @RequestHeader("mcs_player_z") double z,
            @RequestHeader("mcs_player_yaw") double yaw,
            @RequestHeader("mcs_player_pitch") double pitch
    ) {
        PlayerModel playerModel = new PlayerModel();
        playerModel.setUuid(uuid);
        playerModel.setName(name);
        playerModel.setServer(server);
        playerModel.setWorld(world);
        playerModel.setX(x);
        playerModel.setY(y);
        playerModel.setZ(z);
        playerModel.setYaw(yaw);
        playerModel.setPitch(pitch);

        repository.save(playerModel);

        LOGGER.info("POST PLAYER --> Server: " + server + "   Name: " + name);

        return ResponseEntity.ok(playerModel);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity delete(
            @PathVariable String uuid
    ) {

        Optional<PlayerModel> optional = repository.findByUuid(uuid);
        if(optional.isEmpty()) return ResponseEntity.badRequest().body("UUID not found!");
        PlayerModel model = optional.get();
        repository.delete(model);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{uuidReq}")
    public ResponseEntity put(
            @PathVariable String uuidReq,
            @RequestHeader(value = "mcs_player_uuid", required = false) Optional<String> uuid,
            @RequestHeader(value = "mcs_player_name", required = false) Optional<String> name,
            @RequestHeader(value = "mcs_player_server", required = false) Optional<String> server,
            @RequestHeader(value = "mcs_player_world", required = false) Optional<String> world,
            @RequestHeader(value = "mcs_player_x", required = false) Optional<Double> x,
            @RequestHeader(value = "mcs_player_y", required = false) Optional<Double> y,
            @RequestHeader(value = "mcs_player_z", required = false) Optional<Double> z,
            @RequestHeader(value = "mcs_player_yaw", required = false) Optional<Double> yaw,
            @RequestHeader(value = "mcs_player_pitch", required = false) Optional<Double> pitch
    ) {

        Optional<PlayerModel> optional = repository.findByUuid(uuidReq);
        if(optional.isEmpty()) return ResponseEntity.badRequest().body("UUID not found!");
        PlayerModel model = optional.get();


        if(uuid.isPresent()) {
            model.setUuid(uuid.get());
        }
        if (name.isPresent()) {
            model.setName(name.get());
        }
        if (server.isPresent()) {
            model.setServer(server.get());
        }
        if (world.isPresent()) {
            model.setWorld(world.get());
        }

        if (x.isPresent()) {
            model.setX(x.get());
        }
        if (y.isPresent()) {
            model.setY(y.get());
        }
        if (z.isPresent()) {
            model.setZ(z.get());
        }
        if (yaw.isPresent()) {
            model.setYaw(yaw.get());
        }
        if (pitch.isPresent()) {
            model.setPitch(pitch.get());
        }

        repository.save(model);
        return ResponseEntity.ok(model);
    }
}
