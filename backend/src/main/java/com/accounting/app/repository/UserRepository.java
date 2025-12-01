package com.accounting.app.repository;

import com.accounting.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーリポジトリ
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを検索
     * @param email メールアドレス
     * @return ユーザー
     */
    Optional<User> findByEmail(String email);

    /**
     * メールアドレスの存在確認
     * @param email メールアドレス
     * @return 存在する場合true
     */
    boolean existsByEmail(String email);
}
