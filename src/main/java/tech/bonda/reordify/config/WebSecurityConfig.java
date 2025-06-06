package tech.bonda.reordify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // permit open endpoints
                        .requestMatchers(
                                "/**"
                        ).permitAll()
                        // all other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // optionally add HTTP Basic (or later OAuth2/JWT)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
