package tech.bonda.reordify.model;

import jakarta.persistence.*;
import lombok.*;
import tech.bonda.reordify.model.common.UUIDEntity;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spotify_tokens")
public class SpotifyToken extends UUIDEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private SpotifyUser user;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private Instant expiry;
}