package com.accounting.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * JWTトークンの生成・検証を行うコンポーネント
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // デフォルト: 24時間
    private long validityInMilliseconds;

    /**
     * JWTトークンを検証する
     *
     * @param token JWTトークン
     * @return トークンが有効な場合true、無効な場合false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);

            logger.debug("JWTトークンの検証に成功しました");
            return true;

        } catch (SecurityException | MalformedJwtException e) {
            logger.error("無効なJWT署名です: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("期限切れのJWTトークンです: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("サポートされていないJWTトークンです: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWTトークンが空です: {}", e.getMessage());
        }

        return false;
    }

    /**
     * JWTトークンからユーザーIDを取得する
     *
     * @param token JWTトークン
     * @return ユーザーID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.get("userId", Long.class);
    }

    /**
     * JWTトークンからメールアドレスを取得する
     *
     * @param token JWTトークン
     * @return メールアドレス
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    /**
     * JWTトークンから認証情報を作成する
     *
     * @param token JWTトークン
     * @return 認証情報
     */
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        Long userId = getUserIdFromToken(token);

        // 簡易的な権限設定（将来的にはDBから取得）
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // UserDetailsの代わりに簡易的なPrincipalオブジェクトを作成
        UserPrincipal principal = new UserPrincipal(userId, email);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    /**
     * JWTトークンを生成する（将来の認証実装用）
     *
     * @param userId ユーザーID
     * @param email メールアドレス
     * @return JWTトークン
     */
    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * 署名用の秘密鍵を取得する
     *
     * @return 秘密鍵
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 認証ユーザー情報を保持するクラス
     */
    public static class UserPrincipal {
        private final Long userId;
        private final String email;

        public UserPrincipal(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }
    }
}
