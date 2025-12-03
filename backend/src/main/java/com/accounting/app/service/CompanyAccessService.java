package com.accounting.app.service;

import com.accounting.app.entity.UserCompanyRole;
import com.accounting.app.exception.ForbiddenException;
import com.accounting.app.repository.UserCompanyRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会社アクセス権限チェックサービス
 */
@Service
@Transactional(readOnly = true)
public class CompanyAccessService {

    private final UserCompanyRoleRepository userCompanyRoleRepository;

    public CompanyAccessService(UserCompanyRoleRepository userCompanyRoleRepository) {
        this.userCompanyRoleRepository = userCompanyRoleRepository;
    }

    /**
     * ユーザーが会社にアクセス権限があるか確認
     * @param userId ユーザーID
     * @param companyId 会社ID
     * @return アクセス可能な場合true
     */
    public boolean hasAccess(Long userId, Long companyId) {
        return userCompanyRoleRepository
                .findByUserIdAndCompanyId(userId, companyId)
                .isPresent();
    }

    /**
     * ユーザーが会社にアクセス権限があるか確認し、なければ例外をスロー
     * @param userId ユーザーID
     * @param companyId 会社ID
     * @throws ForbiddenException アクセス権限がない場合
     */
    public void validateAccess(Long userId, Long companyId) {
        if (!hasAccess(userId, companyId)) {
            throw new ForbiddenException("この会社へのアクセス権限がありません");
        }
    }

    /**
     * ユーザーの会社ロール情報を取得
     * @param userId ユーザーID
     * @param companyId 会社ID
     * @return ユーザー会社ロール
     * @throws ForbiddenException アクセス権限がない場合
     */
    public UserCompanyRole getUserCompanyRole(Long userId, Long companyId) {
        return userCompanyRoleRepository
                .findByUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new ForbiddenException("この会社へのアクセス権限がありません"));
    }
}
