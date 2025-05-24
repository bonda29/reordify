package tech.bonda.reordify.auth;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import tech.bonda.reordify.models.SpotifyToken;
import tech.bonda.reordify.models.SpotifyUser;
import tech.bonda.reordify.repository.SpotifyUserRepository;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyAuthService {

    private static final String SCOPES = """
                playlist-modify-private,
                playlist-modify-public,
                playlist-read-private,
                user-library-modify,
                user-read-private,
                user-read-email
            """;

    private final SpotifyUserRepository userRepository;

    @Value("${spotify.client-id}")
    private String clientId;
    @Value("${spotify.client-secret}")
    private String clientSecret;
    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    /**
     * Builds the Spotify authorization URL where the user should be redirected to log in.
     */
    public URI buildLoginUri() {
        // Create a SpotifyApi instance for generating the authorization URL
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();

        return spotifyApi.authorizationCodeUri()
                .scope(SCOPES)
                .build()
                .execute();  // URI to Spotify's login/authorize page
    }

    /**
     * Process the authorization code returned by Spotify and get tokens.
     * Creates or updates a SpotifyUser and SpotifyToken in the database.
     * Returns the SpotifyUser entity for further use (e.g., in response).
     */
    @Transactional
    public SpotifyUser processOAuthCallback(String code) throws IOException, SpotifyWebApiException, ParseException {
        // Exchange the code for an access token and refresh token
        SpotifyApi apiForCode = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
        AuthorizationCodeCredentials authCredentials = apiForCode
                .authorizationCode(code)
                .build()
                .execute();

        // Extract tokens and expiration
        String accessToken = authCredentials.getAccessToken();
        String refreshToken = authCredentials.getRefreshToken();
        int expiresIn = authCredentials.getExpiresIn();  // seconds until expiration

        // Use the access token to get the current user's Spotify profile (to identify the user)
        SpotifyApi apiWithAccess = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build();

        User spotifyProfile = apiWithAccess.getCurrentUsersProfile().build().execute();
        String spotifyUserId = spotifyProfile.getId();
        String displayName = spotifyProfile.getDisplayName();
        Instant expiryTime = Instant.now().plusSeconds(expiresIn);

        Optional<SpotifyUser> existingUserOpt = userRepository.findBySpotifyId(spotifyUserId);
        SpotifyUser user;
        if (existingUserOpt.isPresent()) {
            // Existing user - update their token info
            user = existingUserOpt.get();
            user.setDisplayName(displayName);
            SpotifyToken token = user.getToken();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setExpiry(expiryTime);
        } else {
            // New user - create user and token
            user = SpotifyUser.builder()
                    .spotifyId(spotifyUserId)
                    .displayName(displayName)
                    .build();
            SpotifyToken token = SpotifyToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiry(expiryTime)
                    .build();
            user.setToken(token);
        }
        user = userRepository.save(user);  // save new or updated user (cascade saves token)
        return user;
    }
}
