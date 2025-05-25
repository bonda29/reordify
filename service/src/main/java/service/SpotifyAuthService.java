package service;

import model.SpotifyUser;

import java.net.URI;

public interface SpotifyAuthService {
    URI getAuthorizationUri();
    SpotifyUser processCallback(String code);
}