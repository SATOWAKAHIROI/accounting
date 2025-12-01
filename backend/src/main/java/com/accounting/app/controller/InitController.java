package com.accounting.app.controller;

import com.accounting.app.dto.common.ApiResponse;
import com.accounting.app.entity.*;
import com.accounting.app.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * データベース初期化コントローラー
 * 初回デプロイ時にテストデータを投入するためのエンドポイント
 */
@RestController
@RequestMapping("/init")
public class InitController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyRoleRepository userCompanyRoleRepository;
    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final AccountRepository accountRepository;

    public InitController(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            UserCompanyRoleRepository userCompanyRoleRepository,
            FiscalPeriodRepository fiscalPeriodRepository,
            AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userCompanyRoleRepository = userCompanyRoleRepository;
        this.fiscalPeriodRepository = fiscalPeriodRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * データベースを初期化
     */
    @PostMapping("/database")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> initDatabase(
            @RequestParam(required = false) String secret) {

        // 簡易的なセキュリティチェック（環境変数で設定可能）
        String expectedSecret = System.getenv("INIT_SECRET");
        if (expectedSecret != null && !expectedSecret.isEmpty() && !expectedSecret.equals(secret)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "無効なシークレットキーです"));
        }

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 会社を作成
            Company company = createCompany();
            result.put("company", company.getName());

            // 2. テストユーザーを作成
            User user = createUser(company);
            result.put("user", user.getUsername());

            // 3. 会計期間を作成
            FiscalPeriod fiscalPeriod = createFiscalPeriod(company);
            result.put("fiscalPeriod", fiscalPeriod.getPeriodName());

            // 4. 基本的な勘定科目を作成
            int accountCount = createBasicAccounts(company);
            result.put("accountsCreated", accountCount);

            result.put("message", "データベースの初期化が完了しました");
            result.put("status", "success");

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("status", "failed");
            return ResponseEntity.status(500)
                .body(ApiResponse.error("INIT_ERROR", "初期化中にエラーが発生しました: " + e.getMessage()));
        }
    }

    /**
     * 初期化状態を確認
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("usersCount", userRepository.count());
        status.put("companiesCount", companyRepository.count());
        status.put("fiscalPeriodsCount", fiscalPeriodRepository.count());
        status.put("accountsCount", accountRepository.count());
        status.put("initialized", userRepository.count() > 0);

        return ResponseEntity.ok(ApiResponse.success(status));
    }

    // ===== プライベートメソッド =====

    private Company createCompany() {
        // 既に存在する場合はスキップ
        if (companyRepository.count() > 0) {
            return companyRepository.findAll().get(0);
        }

        Company company = new Company();
        company.setName("テスト株式会社");
        company.setCode("TEST001");
        company.setRepresentativeName("代表太郎");
        company.setPostalCode("100-0001");
        company.setAddress("東京都千代田区千代田1-1");
        company.setPhoneNumber("03-1234-5678");
        company.setEmail("info@test-company.co.jp");
        company.setFiscalYearEndMonth(12);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());

        return companyRepository.save(company);
    }

    private User createUser(Company company) {
        // 既に存在する場合はスキップ
        if (userRepository.findByUsername("admin").isPresent()) {
            return userRepository.findByUsername("admin").get();
        }

        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        // 注: 実際の本番環境ではパスワードハッシュ化が必要
        user.setPasswordHash("password123");
        user.setDisplayName("管理者");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // ユーザーと会社の関連付け
        UserCompanyRole role = new UserCompanyRole();
        role.setUser(user);
        role.setCompany(company);
        role.setRole(UserCompanyRole.Role.ADMIN);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        userCompanyRoleRepository.save(role);

        return user;
    }

    private FiscalPeriod createFiscalPeriod(Company company) {
        // 既に存在する場合はスキップ
        if (fiscalPeriodRepository.findByCompanyIdOrderByStartDate(company.getId()).size() > 0) {
            return fiscalPeriodRepository.findByCompanyIdOrderByStartDate(company.getId()).get(0);
        }

        FiscalPeriod period = new FiscalPeriod();
        period.setCompany(company);
        period.setPeriodYear(2025);
        period.setPeriodNumber(1);
        period.setPeriodName("2025年第一期");
        period.setStartDate(LocalDate.of(2025, 1, 1));
        period.setEndDate(LocalDate.of(2025, 12, 31));
        period.setIsClosed(false);
        period.setCreatedAt(LocalDateTime.now());
        period.setUpdatedAt(LocalDateTime.now());

        return fiscalPeriodRepository.save(period);
    }

    private int createBasicAccounts(Company company) {
        // 既に存在する場合はスキップ
        if (accountRepository.findByCompanyIdOrderByCode(company.getId()).size() > 0) {
            return accountRepository.findByCompanyIdOrderByCode(company.getId()).size();
        }

        int count = 0;

        // 資産科目
        count += createAccount(company, "1000", "現金", Account.AccountType.ASSET, false);
        count += createAccount(company, "1010", "普通預金", Account.AccountType.ASSET, false);
        count += createAccount(company, "1100", "売掛金", Account.AccountType.ASSET, false);
        count += createAccount(company, "1200", "商品", Account.AccountType.ASSET, false);

        // 負債科目
        count += createAccount(company, "2000", "買掛金", Account.AccountType.LIABILITY, false);
        count += createAccount(company, "2100", "未払金", Account.AccountType.LIABILITY, false);
        count += createAccount(company, "2200", "借入金", Account.AccountType.LIABILITY, false);

        // 純資産科目
        count += createAccount(company, "3000", "資本金", Account.AccountType.EQUITY, false);
        count += createAccount(company, "3100", "繰越利益剰余金", Account.AccountType.EQUITY, false);

        // 収益科目
        count += createAccount(company, "4000", "売上高", Account.AccountType.REVENUE, false);
        count += createAccount(company, "4100", "受取利息", Account.AccountType.REVENUE, false);

        // 費用科目
        count += createAccount(company, "5000", "仕入高", Account.AccountType.EXPENSE, false);
        count += createAccount(company, "5100", "給料手当", Account.AccountType.EXPENSE, false);
        count += createAccount(company, "5200", "地代家賃", Account.AccountType.EXPENSE, false);
        count += createAccount(company, "5300", "水道光熱費", Account.AccountType.EXPENSE, false);

        return count;
    }

    private int createAccount(Company company, String code, String name,
                             Account.AccountType type, boolean isSystem) {
        Account account = new Account();
        account.setCompany(company);
        account.setCode(code);
        account.setName(name);
        account.setAccountType(type);
        account.setIsSystem(isSystem);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);
        return 1;
    }
}
