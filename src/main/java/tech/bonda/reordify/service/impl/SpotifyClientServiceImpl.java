package tech.bonda.reordify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import tech.bonda.reordify.exception.SpotifyApiException;
import tech.bonda.reordify.model.SpotifyToken;
import tech.bonda.reordify.model.SpotifyUser;
import tech.bonda.reordify.repository.SpotifyUserRepository;
import tech.bonda.reordify.service.SpotifyClientService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpotifyClientServiceImpl implements SpotifyClientService {
    private final ObjectProvider<SpotifyApi.Builder> builderProvider;
    private final SpotifyUserRepository userRepo;

    @Override
    @Transactional
    @SneakyThrows
    public SpotifyApi getApiForUser(UUID userId) {
        SpotifyUser user = userRepo.findById(userId)
                .orElseThrow(() -> new SpotifyApiException("User not found: " + userId));
        SpotifyToken token = user.getToken();

        // Refresh if expired
        if (token.getExpiry().isBefore(Instant.now())) {
            SpotifyApi refreshApi = builderProvider.getObject()
                    .setRefreshToken(token.getRefreshToken())
                    .build();
            AuthorizationCodeCredentials newCreds = refreshApi.authorizationCodeRefresh()
                    .build().execute();

            token.setAccessToken(newCreds.getAccessToken());
            token.setExpiry(Instant.now().plusSeconds(newCreds.getExpiresIn()));
            if (newCreds.getRefreshToken() != null) {
                token.setRefreshToken(newCreds.getRefreshToken());
            }
            userRepo.save(user);
        }

        // Return a clean client with updated tokens
        return builderProvider.getObject()
                .setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken())
                .build();
    }
}

