package repository;


import exeption.UserNotFoundException;
import model.SpotifyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpotifyUserRepository extends JpaRepository<SpotifyUser, UUID> {
    Optional<SpotifyUser> findBySpotifyId(String spotifyId);

    default SpotifyUser findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
