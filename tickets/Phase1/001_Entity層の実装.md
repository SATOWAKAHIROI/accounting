# チケット #001: Entity層の実装

## ステータス
- [x] 完了

## 機能概要
14テーブルに対応するJPAエンティティクラスの作成。
各エンティティには、データベーステーブルのカラムに対応するフィールド、JPA/Hibernateアノテーション、タイムスタンプ自動設定（@PrePersist, @PreUpdate）を含む。

## 実装ファイル

### backend/src/main/java/com/accounting/app/entity/
- [x] `User.java` - ユーザー情報
- [x] `Company.java` - 会社情報
- [x] `UserCompanyRole.java` - ユーザー・会社・ロール関連
- [x] `FiscalPeriod.java` - 会計期間
- [x] `Account.java` - 勘定科目
- [x] `SubAccount.java` - 補助科目
- [x] `TaxType.java` - 税区分
- [x] `Partner.java` - 取引先
- [x] `Item.java` - 品目
- [x] `Journal.java` - 仕訳ヘッダー
- [x] `JournalDetail.java` - 仕訳明細
- [x] `Invoice.java` - 請求書ヘッダー
- [x] `InvoiceDetail.java` - 請求書明細
- [x] `Payment.java` - 入金情報

## 主要な実装内容

### エンティティの共通仕様
- `@Entity` アノテーション
- `@Table` アノテーションでテーブル名を指定
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` で主キー設定
- `created_at`, `updated_at` フィールドの自動設定
- 列挙型（Enum）の使用: RoleType, AccountType, PartnerType, InvoiceStatus, PaymentMethod等

### リレーションシップ
- `@ManyToOne` - 多対一のリレーション（例: Journal → Company）
- `@OneToMany` - 一対多のリレーション（例: Journal → JournalDetail）
- `@JoinColumn` - 外部キーの指定
- `cascade = CascadeType.ALL` - カスケード操作
- `orphanRemoval = true` - 孤立エンティティの自動削除

## 備考
- すべてのエンティティクラスはgetters/settersを手動実装（Lombokは使用しない方針）
- タイムスタンプは`LocalDateTime`を使用
- 日付フィールドは`LocalDate`を使用
- 金額フィールドは`BigDecimal`を使用
