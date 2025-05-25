package model;

import model.common.UUIDEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spotify_users")
public class SpotifyUser extends UUIDEntity {
    @Column(name = "spotify_id", unique = true, nullable = false)
    private String spotifyId;

    private String displayName;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private SpotifyToken token;
}