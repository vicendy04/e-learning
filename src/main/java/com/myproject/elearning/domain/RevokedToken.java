package com.myproject.elearning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    String jti;

    @Column(nullable = false)
    Instant expireTime;

    public static RevokedToken from(String jti, Instant expireTime) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setJti(jti);
        revokedToken.setExpireTime(expireTime);
        return revokedToken;
    }
}
