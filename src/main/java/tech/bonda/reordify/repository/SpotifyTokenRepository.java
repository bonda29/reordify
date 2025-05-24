package tech.bonda.reordify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.bonda.reordify.model.SpotifyToken;

import java.util.UUID;

@Repository
public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, UUID> {
}
