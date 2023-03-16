package com.catchtwobirds.soboro.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_refresh_token")
public class UserRefreshToken {
    @JsonIgnore
    @Id
    @Column(name = "refresh_token_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @Column(name = "user_id", length = 64, unique = true)
    @NotNull
    @Size(max = 64)
    private String userId;

    @Column(name = "refresh_token", length = 256)
    @NotNull
    @Size(max = 256)
    private String refreshToken;

    public UserRefreshToken(
            @NotNull @Size(max = 64) String userId,
            @NotNull @Size(max = 256) String refreshToken
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
