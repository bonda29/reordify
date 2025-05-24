package tech.bonda.reordify.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import tech.bonda.reordify.common.models.UUIDEntity;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spotify_tokens")
public class SpotifyToken extends UUIDEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private SpotifyUser user;

    private String accessToken;
    private String refreshToken;
    private Instant expiry;
}
