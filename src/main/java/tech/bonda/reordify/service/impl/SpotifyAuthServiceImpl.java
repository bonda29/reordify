package tech.bonda.reordify.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import tech.bonda.reordify.config.SpotifyProperties;
import tech.bonda.reordify.exception.SpotifyApiException;
import tech.bonda.reordify.model.SpotifyToken;
import tech.bonda.reordify.model.SpotifyUser;
import tech.bonda.reordify.repository.SpotifyUserRepository;
import tech.bonda.reordify.service.SpotifyAuthService;

import java.net.URI;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SpotifyAuthServiceImpl implements SpotifyAuthService {
    private final SpotifyApi.Builder builder;
    private final SpotifyProperties props;
    private final SpotifyUserRepository userRepo;

    @Override
    public URI getAuthorizationUri() {
        try {
            SpotifyApi api = builder.build();
            return api.authorizationCodeUri()
                    .scope(String.join(" ", props.getScopes()))
                    .build()
                    .execute();
        } catch (Exception e) {
            throw new SpotifyApiException("Failed to build authorization URI", e);
        }
    }

    @Override
    @Transactional
    public SpotifyUser processCallback(String code) {
        try {
            // Exchange code for tokens
            SpotifyApi api = builder.build();
            AuthorizationCodeCredentials creds = api.authorizationCode(code)
                    .build()
                    .execute();

            String accessToken = creds.getAccessToken();
            String refreshToken = creds.getRefreshToken();
            Instant expiry = Instant.now().plusSeconds(creds.getExpiresIn());

            // Fetch Spotify user profile
            SpotifyApi apiWithToken = builder
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .build();
            var profile = apiWithToken.getCurrentUsersProfile().build().execute();

            // Upsert user & token
            return userRepo.save(userRepo.findBySpotifyId(profile.getId())
                    .map(user -> {
                        user.setDisplayName(profile.getDisplayName());
                        SpotifyToken token = user.getToken();
                        token.setAccessToken(accessToken);
                        token.setRefreshToken(refreshToken);
                        token.setExpiry(expiry);
                        return user;
                    })
                    .orElseGet(() -> {
                        SpotifyUser newUser = SpotifyUser.builder()
                                .spotifyId(profile.getId())
                                .displayName(profile.getDisplayName())
                                .token(SpotifyToken.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .expiry(expiry)
                                        .build())
                                .build();
                        newUser.getToken().setUser(newUser);
                        return newUser;
                    }));
        } catch (Exception e) {
            throw new SpotifyApiException("Failed to process Spotify callback", e);
        }
    }
}