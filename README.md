# 会計ソフト

複式簿記に対応した会計ソフトウェア。学習・ポートフォリオ用途として開発。

## 技術スタック

### バックエンド
- **言語**: Java 11
- **フレームワーク**: Spring Boot 2.7.18
- **データベース**: MySQL 8.0
- **認証**: OAuth 2.0 / OpenID Connect
- **PDF生成**: Apache PDFBox
- **Excel生成**: Apache POI

### フロントエンド
- **言語**: JavaScript (ES6+)
- **フレームワーク**: React 18
- **ビルドツール**: Vite
- **スタイリング**: Tailwind CSS
- **ルーティング**: React Router
- **状態管理**: React Context + Hooks
- **HTTP通信**: Axios

### インフラ
- **コンテナ**: Docker / Docker Compose
- **CI/CD**: GitHub Actions
- **デプロイ**: Heroku

## プロジェクト構成

```
accounting-app/
├── backend/              # Spring Bootアプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/accounting/app/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/            # Reactアプリケーション
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── contexts/
│   │   ├── services/
│   │   ├── utils/
│   │   └── styles/
│   ├── package.json
│   ├── Dockerfile
│   └── Dockerfile.dev
├── docker-compose.yml
├── .github/workflows/
└── README.md
```

## 開発環境のセットアップ

### 前提条件

以下のソフトウェアがインストールされている必要があります：

- Docker Desktop (Docker & Docker Compose)
- Git

### セットアップ手順

1. **リポジトリのクローン**

```bash
git clone <repository-url>
cd accounting-app
```

2. **環境変数の設定**

フロントエンドの環境変数ファイルを作成：

```bash
cd frontend
cp .env.example .env
cd ..
```

3. **Docker Composeで起動**

```bash
docker-compose up -d
```

初回起動時は、イメージのビルドに時間がかかります。

4. **アプリケーションへのアクセス**

- **フロントエンド**: http://localhost:3000
- **バックエンドAPI**: http://localhost:8080/api
- **MySQL**: localhost:3306

5. **停止**

```bash
docker-compose down
```

データを削除する場合：

```bash
docker-compose down -v
```

## ローカル開発（Docker Composeを使わない場合）

### バックエンド

```bash
cd backend
./mvnw spring-boot:run
```

### フロントエンド

```bash
cd frontend
npm install
npm run dev
```

## 主要機能（MVP）

### ① マスタ設定系
- 勘定科目マスタ（資産・負債・純資産・収益・費用）
- 補助科目マスタ
- 取引先マスタ
- 税区分マスタ（軽減税率・標準税率対応）
- 品目/サービスマスタ

### ② 仕訳入力
- 複式簿記の仕訳入力
- 消費税処理（外税/内税/非課税）
- 仕訳テンプレート
- 現金出納帳・預金出納帳風入力

### ③ 帳簿・レポート出力
- 総勘定元帳
- 仕訳帳
- 試算表（合計残高試算表）
- 損益計算書（PL）
- 貸借対照表（BS）
- 現金出納帳・預金出納帳
- 売掛金元帳・買掛金元帳
- CSV/Excelエクスポート

### ④ 消費税対応
- 税区分・税率管理
- 課税売上・課税仕入の集計

### ⑤ 会計期間・締め処理
- 会計期間管理
- 月次締め/年度締め
- 締め後の編集制御

### ⑥ 請求書機能
- 請求書作成
- 請求書PDF出力
- 売掛金管理
- 入金消込

## テスト

### バックエンド

```bash
cd backend
./mvnw test
```

### フロントエンド

```bash
cd frontend
npm run test
```

## デプロイ

GitHub Actionsによる自動デプロイを設定済み。`main`ブランチへのプッシュで自動的にHerokuにデプロイされます。

## ライセンス

このプロジェクトはポートフォリオ用途です。

## 開発者

開発に関する質問や提案は、Issuesまでお願いします。
