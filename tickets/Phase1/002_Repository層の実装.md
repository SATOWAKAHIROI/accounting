# チケット #002: Repository層の実装

## ステータス
- [x] 完了

## 機能概要
Spring Data JPAリポジトリインターフェースの作成。
各エンティティに対応するリポジトリを作成し、基本的なCRUD操作に加えて、カスタムクエリメソッドを定義。

## 実装ファイル

### backend/src/main/java/com/accounting/app/repository/
- [x] `UserRepository.java`
- [x] `CompanyRepository.java`
- [x] `UserCompanyRoleRepository.java`
- [x] `FiscalPeriodRepository.java`
- [x] `AccountRepository.java`
- [x] `SubAccountRepository.java`
- [x] `TaxTypeRepository.java`
- [x] `PartnerRepository.java`
- [x] `ItemRepository.java`
- [x] `JournalRepository.java`
- [x] `JournalDetailRepository.java`
- [x] `InvoiceRepository.java`
- [x] `InvoiceDetailRepository.java`
- [x] `PaymentRepository.java`

## 主要な実装内容

### 基本構造
すべてのリポジトリは`JpaRepository<Entity, Long>`を継承し、以下のメソッドを自動で利用可能：
- `findAll()` - 全件取得
- `findById(Long id)` - ID検索
- `save(Entity entity)` - 保存/更新
- `deleteById(Long id)` - 削除
- `existsById(Long id)` - 存在確認

### カスタムクエリメソッド例

**UserRepository**:
- `Optional<User> findByEmail(String email)` - メールアドレス検索
- `boolean existsByEmail(String email)` - メールアドレス存在確認

**AccountRepository**:
- `List<Account> findByCompanyIdOrderByCode(Long companyId)` - 会社ID検索（コード順）
- `List<Account> findByCompanyIdAndAccountTypeOrderByCode(Long companyId, AccountType accountType)` - タイプ別検索
- `Optional<Account> findByCompanyIdAndCode(Long companyId, String code)` - コード検索
- `boolean existsByCompanyIdAndCode(Long companyId, String code)` - コード存在確認

**JournalRepository**:
- `List<Journal> findByCompanyIdOrderByJournalDateDesc(Long companyId)` - 日付降順
- `List<Journal> findByCompanyIdAndFiscalPeriodIdOrderByJournalDateDesc(Long companyId, Long fiscalPeriodId)` - 会計期間別
- `List<Journal> findByCompanyIdAndJournalDateBetweenOrderByJournalDate(Long companyId, LocalDate startDate, LocalDate endDate)` - 日付範囲検索

**TaxTypeRepository**:
- `@Query` アノテーションを使用したカスタムクエリ
- `findEffectiveTaxTypes(Long companyId, LocalDate date)` - 有効期間内の税区分取得

## 備考
- Spring Data JPAの命名規則に従ったメソッド名で自動クエリ生成
- 複雑なクエリは`@Query`アノテーションを使用
- ソート順は`OrderBy`を使用
- すべてのリポジトリに`@Repository`アノテーションを付与
