package tech.bonda.reordify.controller;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import tech.bonda.reordify.auth.SpotifyAuthService;
import tech.bonda.reordify.models.SpotifyUser;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final SpotifyAuthService authService;

    /**
     * Endpoint to start the Spotify OAuth login flow.
     * Redirects the user's browser to Spotify's authorization page.
     */
    @GetMapping("/login")
    public RedirectView loginWithSpotify() {
        URI redirectUri = authService.buildLoginUri();
        return new RedirectView(redirectUri.toString());
    }

    /**
     * Spotify redirect URI endpoint. Spotify calls this with ?code (or ?error) after user login.
     */
    @GetMapping("/callback")
    public ResponseEntity<?> spotifyCallback(@RequestParam(name = "code", required = false) String code,
                                             @RequestParam(name = "error", required = false) String error) {
        try {
            if (error != null) {
                // User denied or an error occurred during auth
                return ResponseEntity.status(400).body("Spotify authorization error: " + error);
            }
            if (code == null) {
                return ResponseEntity.badRequest().body("Missing authorization code");
            }
            // Process the authorization code to get tokens and user info
            SpotifyUser user = authService.processOAuthCallback(code);
            // Return a simple success message with the user's info (ID and display name)
            return ResponseEntity.ok().body(
                    // respond with JSON containing our internal user ID and Spotify display name
                    java.util.Map.of(
                            "message", "Spotify login successful",
                            "userId", user.getId(),
                            "spotifyId", user.getSpotifyId(),
                            "displayName", user.getDisplayName()
                    )
            );
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during Spotify authorization: " + e.getMessage());
        }
    }
}
