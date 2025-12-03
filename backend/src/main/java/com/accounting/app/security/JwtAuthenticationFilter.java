package com.accounting.app.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWTトークンを検証し、認証情報をSecurityContextにセットするフィルター
 * 全てのHTTPリクエストに対して1回だけ実行される
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * リクエストごとに実行される認証処理
     *
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param filterChain フィルターチェーン
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. リクエストヘッダーからJWTトークンを取得
            String jwt = getJwtFromRequest(request);

            // 2. トークンが存在し、有効な場合
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // 3. トークンから認証情報を取得
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

                // 4. SecurityContextに認証情報をセット
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // デバッグログ
                JwtTokenProvider.UserPrincipal principal =
                    (JwtTokenProvider.UserPrincipal) authentication.getPrincipal();
                logger.debug("認証成功: userId={}, email={}, path={}",
                    principal.getUserId(),
                    principal.getEmail(),
                    request.getRequestURI());
            } else {
                logger.debug("JWTトークンが見つからないか無効です: path={}", request.getRequestURI());
            }

        } catch (Exception ex) {
            logger.error("SecurityContextに認証情報をセットできませんでした", ex);
        }

        // 5. 次のフィルターへ処理を渡す
        filterChain.doFilter(request, response);
    }

    /**
     * HTTPリクエストのAuthorizationヘッダーからJWTトークンを抽出する
     *
     * @param request HTTPリクエスト
     * @return JWTトークン（Bearer プレフィックスを除く）
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Bearer " で始まる場合、その部分を除いたトークンを返す
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.debug("Authorizationヘッダーからトークンを抽出: {}", token.substring(0, Math.min(20, token.length())) + "...");
            return token;
        }

        return null;
    }
}
