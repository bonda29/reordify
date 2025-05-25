package controller.v1;

import exeption.SpotifyApiException;
import lombok.RequiredArgsConstructor;
import model.SpotifyUser;
import model.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import service.SpotifyAuthService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final SpotifyAuthService authService;

    @GetMapping("/login")
    public RedirectView login() {
        URI uri = authService.getAuthorizationUri();
        return new RedirectView(uri.toString());
    }

    @GetMapping("/callback")
    public ResponseEntity<AuthResponse> callback(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "error", required = false) String error
    ) {
        if (error != null) {
            throw new SpotifyApiException("Spotify authorization error: " + error);
        }
        if (code == null) {
            throw new SpotifyApiException("Missing authorization code");
        }
        SpotifyUser user = authService.processCallback(code);
        return ResponseEntity.ok(
                new AuthResponse(user.getId(), user.getSpotifyId(), user.getDisplayName())
        );
    }
}