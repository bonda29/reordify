package tech.bonda.reordify.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Configuration
public class SpotifyConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SpotifyApi.Builder spotifyApiBuilder(SpotifyProperties props) {
        return new SpotifyApi.Builder()
                .setClientId(props.getClientId())
                .setClientSecret(props.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(props.getRedirectUri()));
    }
}
