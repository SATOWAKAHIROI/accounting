package com.accounting.app.service;

import com.accounting.app.dto.request.LoginRequest;
import com.accounting.app.dto.request.UserRequest;
import com.accounting.app.dto.response.AuthResponse;
import com.accounting.app.dto.response.UserResponse;
import com.accounting.app.entity.User;
import com.accounting.app.entity.UserCompanyRole;
import com.accounting.app.entity.Company;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.CompanyRepository;
import com.accounting.app.repository.UserCompanyRoleRepository;
import com.accounting.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ユーザー管理サービス
 *
 * TODO: 以下の機能を実装する必要があります
 * 1. Spring Securityの設定
 * 2. BCryptPasswordEncoderによるパスワードハッシュ化
 * 3. JWTトークンの生成・検証
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserCompanyRoleRepository userCompanyRoleRepository;
    private final CompanyRepository companyRepository;
    // TODO: PasswordEncoderを追加
    // private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserCompanyRoleRepository userCompanyRoleRepository,
            CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.userCompanyRoleRepository = userCompanyRoleRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 全件取得
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * ID検索
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.from(user);
    }

    /**
     * メールアドレスで検索
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponse.from(user);
    }

    /**
     * 作成
     */
    public UserResponse create(UserRequest request) {
        // メールアドレスの重複チェック
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("メールアドレス '" + request.getEmail() + "' は既に使用されています");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // TODO: パスワードのハッシュ化を実装
        // user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // 暫定的に平文で保存（本番では絶対に使用しないこと）
        user.setPasswordHash(request.getPassword());

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    /**
     * 更新
     */
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // メールアドレスの重複チェック（自身を除く）
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new BadRequestException("メールアドレス '" + request.getEmail() + "' は既に使用されています");
                    }
                });

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // パスワードが提供されている場合のみ更新
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // TODO: パスワードのハッシュ化を実装
            // user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setPasswordHash(request.getPassword());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.from(updatedUser);
    }

    /**
     * 削除
     */
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // ユーザー・会社ロールを削除
        userCompanyRoleRepository.deleteByUserId(id);

        userRepository.delete(user);
    }

    /**
     * 認証
     * TODO: JWT実装が必要
     */
    public AuthResponse authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("メールアドレスまたはパスワードが正しくありません"));

        // TODO: パスワード検証を実装
        // if (!passwordEncoder.matches(password, user.getPasswordHash())) {
        //     throw new BadRequestException("メールアドレスまたはパスワードが正しくありません");
        // }

        // 暫定的な実装（本番では使用しないこと）
        if (!user.getPasswordHash().equals(password)) {
            throw new BadRequestException("メールアドレスまたはパスワードが正しくありません");
        }

        // TODO: JWTトークンの生成を実装
        String token = "dummy-jwt-token"; // 仮のトークン

        return new AuthResponse(token, UserResponse.from(user));
    }

    /**
     * ロール割り当て
     */
    public void assignRole(Long userId, Long companyId, UserCompanyRole.RoleType role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // 既存のロールを確認
        userCompanyRoleRepository.findByUserIdAndCompanyId(userId, companyId)
                .ifPresent(existingRole -> {
                    // 既存のロールを更新
                    existingRole.setRole(role);
                    userCompanyRoleRepository.save(existingRole);
                    return;
                });

        // 新規ロールを作成
        UserCompanyRole userCompanyRole = new UserCompanyRole();
        userCompanyRole.setUser(user);
        userCompanyRole.setCompany(company);
        userCompanyRole.setRole(role);

        userCompanyRoleRepository.save(userCompanyRole);
    }
}
