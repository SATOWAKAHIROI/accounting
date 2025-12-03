package com.accounting.app.service;

import com.accounting.app.security.JwtTokenProvider.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 現在の認証ユーザー情報を取得するヘルパーサービス
 */
@Service
public class CurrentUserService {

    /**
     * 現在の認証ユーザー情報を取得
     * @return UserPrincipal
     * @throws IllegalStateException 認証されていない場合
     */
    public UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("認証されていません");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new IllegalStateException("認証情報が不正です");
        }

        return (UserPrincipal) principal;
    }

    /**
     * 現在の認証ユーザーIDを取得
     * @return ユーザーID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * 現在の認証ユーザーのメールアドレスを取得
     * @return メールアドレス
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
