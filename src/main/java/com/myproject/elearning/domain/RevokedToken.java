package com.myproject.elearning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    private String jti;

    @Column(nullable = false)
    private Instant expireTime;

    public static RevokedToken from(String jti, Instant expireTime) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setJti(jti);
        revokedToken.setExpireTime(expireTime);
        return revokedToken;
    }
}
