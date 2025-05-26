package tech.bonda.reordify.service;

import se.michaelthelin.spotify.model_objects.specification.Playlist;

import java.util.List;
import java.util.UUID;

public interface SpotifyPlaylistService {
    Playlist getPlaylist(UUID userId, String playlistId);
    List<Playlist> getAllPlaylists(UUID userId);
}
