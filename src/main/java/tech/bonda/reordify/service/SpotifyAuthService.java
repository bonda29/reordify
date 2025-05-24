package tech.bonda.reordify.service;

import tech.bonda.reordify.model.SpotifyUser;
import java.net.URI;

public interface SpotifyAuthService {
    URI getAuthorizationUri();
    SpotifyUser processCallback(String code);
}