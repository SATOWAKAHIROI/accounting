package com.accounting.app.repository;

import com.accounting.app.entity.UserCompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ユーザー会社ロールリポジトリ
 */
@Repository
public interface UserCompanyRoleRepository extends JpaRepository<UserCompanyRole, Long> {

    /**
     * ユーザーIDで検索
     * @param userId ユーザーID
     * @return ユーザー会社ロールリスト
     */
    List<UserCompanyRole> findByUserId(Long userId);

    /**
     * 会社IDで検索
     * @param companyId 会社ID
     * @return ユーザー会社ロールリスト
     */
    List<UserCompanyRole> findByCompanyId(Long companyId);

    /**
     * ユーザーIDと会社IDで検索
     * @param userId ユーザーID
     * @param companyId 会社ID
     * @return ユーザー会社ロール
     */
    Optional<UserCompanyRole> findByUserIdAndCompanyId(Long userId, Long companyId);

    /**
     * 会社IDで削除
     * @param companyId 会社ID
     */
    void deleteByCompanyId(Long companyId);

    /**
     * ユーザーIDで削除
     * @param userId ユーザーID
     */
    void deleteByUserId(Long userId);
}
