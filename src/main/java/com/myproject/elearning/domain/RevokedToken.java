package com.myproject.elearning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "revoked_tokens")
@NoArgsConstructor
@AllArgsConstructor
public class RevokedToken {

    @Id
    private String jti;

    @Column(nullable = false)
    private Instant expireTime;

    //    @Column(nullable = false)
    //    private TokenType tokenType;
    //
    //    public enum TokenType {
    //        ACCESS_TOKEN, REFRESH_TOKEN
    //    }
}
