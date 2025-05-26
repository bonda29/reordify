package tech.bonda.reordify.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import tech.bonda.reordify.service.SpotifyPlaylistService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
public class PlaylistController {
    private final SpotifyPlaylistService playlistService;

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists(@RequestParam("user_id") UUID userId) {
        List<Playlist> playlists = playlistService.getAllPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable String playlistId, @RequestParam("user_id") UUID userId) {
        Playlist playlist = playlistService.getPlaylist(userId, playlistId);
        return ResponseEntity.ok(playlist);
    }
}