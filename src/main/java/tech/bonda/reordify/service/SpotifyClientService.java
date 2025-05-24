package tech.bonda.reordify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import tech.bonda.reordify.models.SpotifyToken;
import tech.bonda.reordify.models.SpotifyUser;
import tech.bonda.reordify.repository.SpotifyUserRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpotifyClientService {

    private final SpotifyUserRepository userRepository;

    @Value("${spotify.client-id}")
    private String clientId;
    @Value("${spotify.client-secret}")
    private String clientSecret;

    /**
     * Get a SpotifyApi instance for the given user, refreshing the token if needed.
     *
     * @param userId The internal ID of the SpotifyUser (from our database).
     * @return a SpotifyApi configured with a valid access token for the user.
     * @throws IllegalStateException if user not found.
     */
    @Transactional
    public SpotifyApi getSpotifyApiForUser(UUID userId) throws Exception {
        SpotifyUser user = userRepository.findByIdOrThrow(userId);
        SpotifyToken token = user.getToken();
        // Check if access token is expired or about to expire
        if (token.getExpiry().isBefore(Instant.now())) {
            // Token expired – refresh it using the stored refresh token
            SpotifyApi refreshApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(token.getRefreshToken())
                    .build();
            AuthorizationCodeCredentials newCreds = refreshApi.authorizationCodeRefresh().build().execute();
            // Update tokens in database
            token.setAccessToken(newCreds.getAccessToken());
            token.setExpiry(Instant.now().plusSeconds(newCreds.getExpiresIn()));
            if (newCreds.getRefreshToken() != null && !newCreds.getRefreshToken().isEmpty()) {
                // Spotify typically returns the same refresh token, but if it provided a new one, update it
                token.setRefreshToken(newCreds.getRefreshToken());
            }
            userRepository.save(user);  // save updated token info
        }
        // Now build a SpotifyApi with the valid token
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken())
                .build();
    }
}
