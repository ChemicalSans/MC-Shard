package net.nussi.mcs.shard.rest.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerModel, Integer> {
    @Query("select p from PlayerModel p where p.uuid like ?1")
    Optional<PlayerModel> findByUuid(@NonNull String uuid);
}