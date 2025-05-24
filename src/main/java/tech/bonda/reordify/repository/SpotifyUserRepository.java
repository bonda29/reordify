package tech.bonda.reordify.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.bonda.reordify.exception.UserNotFoundException;
import tech.bonda.reordify.model.SpotifyUser;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpotifyUserRepository extends JpaRepository<SpotifyUser, UUID> {
    Optional<SpotifyUser> findBySpotifyId(String spotifyId);

    default SpotifyUser findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
