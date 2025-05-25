package service;

import se.michaelthelin.spotify.SpotifyApi;
import java.util.UUID;

public interface SpotifyClientService {
    SpotifyApi getApiForUser(UUID userId);
}