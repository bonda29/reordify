package tech.bonda.reordify.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.bonda.reordify.dto.UserProfileResponse;
import tech.bonda.reordify.service.SpotifyClientService;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final SpotifyClientService clientService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable UUID userId) throws Exception {
        User profile = clientService.getApiForUser(userId)
                .getCurrentUsersProfile()
                .build()
                .execute();
        return ResponseEntity.ok(
                new UserProfileResponse(
                        profile.getId(),
                        profile.getDisplayName(),
                        profile.getEmail()
                )
        );
    }
}