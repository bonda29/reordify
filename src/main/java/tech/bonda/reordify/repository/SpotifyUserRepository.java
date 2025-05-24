package tech.bonda.reordify.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.bonda.reordify.models.SpotifyUser;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpotifyUserRepository extends JpaRepository<SpotifyUser, UUID> {
    Optional<SpotifyUser> findBySpotifyId(String spotifyId);
    default SpotifyUser findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("SpotifyUser not found with id: " + id));
    }
}
