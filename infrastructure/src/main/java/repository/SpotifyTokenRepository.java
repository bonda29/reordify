package repository;

import model.SpotifyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, UUID> {
}
