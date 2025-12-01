# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

複式簿記対応の会計ソフトウェア。学習・ポートフォリオ用途として開発中。

**Tech Stack:**
- Backend: Spring Boot 2.7.18 (Java 11) + MySQL 8.0
- Frontend: React 18 + Vite + TailwindCSS
- Infrastructure: Docker Compose

## Development Commands

### Docker Compose (Recommended)

```bash
# Start all services (MySQL + Backend + Frontend)
docker-compose up -d

# View logs
docker-compose logs -f [service_name]  # backend, frontend, db

# Stop all services
docker-compose down

# Stop and remove volumes (delete all data)
docker-compose down -v

# Restart a specific service
docker-compose restart backend
```

Access:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- MySQL: localhost:3306

### Backend (Spring Boot)

```bash
cd backend

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AccountServiceTest

# Clean and compile
./mvnw clean compile

# Package (create JAR)
./mvnw package
```

### Frontend (React + Vite)

```bash
cd frontend

# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint
npm run lint
```

## Architecture

### Backend Package Structure

```
com.accounting.app/
├── entity/           # JPA entities (14 tables)
├── repository/       # Spring Data JPA repositories
├── dto/
│   ├── request/     # API request DTOs (validation included)
│   ├── response/    # API response DTOs (no sensitive data)
│   └── common/      # ApiResponse, PageResponse
├── service/         # Business logic layer
├── controller/      # REST API endpoints
├── exception/       # Custom exceptions + GlobalExceptionHandler
└── config/          # Spring configuration classes
```

### Frontend Structure

```
src/
├── pages/           # Page components (routed)
│   ├── master/     # Master data screens (Account, SubAccount, etc.)
│   ├── transaction/ # Transaction screens (Journal, Invoice, Payment)
│   ├── report/     # Report screens (GeneralLedger, TrialBalance, etc.)
│   └── settings/   # Settings screens (FiscalPeriod, Company, User)
├── components/      # Reusable UI components
│   └── layout/     # Header, Sidebar, MainLayout
├── services/        # API client layer (Axios-based)
├── contexts/        # React Context (AuthContext)
├── hooks/           # Custom React hooks
└── utils/           # Utility functions (api.js with JWT interceptor)
```

### Key Architectural Patterns

#### DTO Pattern (Backend)

**Request DTOs:** Client → Server (with validation)
```java
@NotBlank
@Size(max = 100)
private String name;
```

**Response DTOs:** Server → Client (exclude sensitive data like passwords)
```java
public static AccountResponse from(Account account) {
    // Map entity to response DTO
}
```

**Never expose Entity directly in API.**

#### Dependency Injection (Spring)

Constructor injection is used throughout. **@Autowired is omitted** (Spring 4.3+ auto-injects single-constructor classes).

```java
public class AccountService {
    private final AccountRepository accountRepository;

    // No @Autowired needed
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

#### Authentication Flow (Frontend)

1. JWT tokens stored in `localStorage`
2. `AuthContext` provides global auth state
3. `utils/api.js` Axios instance automatically attaches tokens
4. Protected routes redirect to login if unauthenticated

### Database Schema

14 tables implementing double-entry bookkeeping:

**Master Data:**
- `users`, `companies`, `user_company_roles`
- `fiscal_periods`, `accounts`, `sub_accounts`
- `tax_types`, `partners`, `items`

**Transaction Data:**
- `journals` (仕訳ヘッダー) ← `journal_details` (明細, 複式簿記)
- `invoices` (請求書) ← `invoice_details`
- `payments` (入金)

### Business Rules

#### Double-Entry Bookkeeping (複式簿記)

Each `Journal` must have balanced debit/credit:
```java
// JournalService validates:
BigDecimal debitTotal = ...;
BigDecimal creditTotal = ...;
if (debitTotal.compareTo(creditTotal) != 0) {
    throw new BusinessException("DEBIT_CREDIT_MISMATCH", ...);
}
```

#### Fiscal Period Locking

Closed fiscal periods prevent modifications:
```java
if (journal.getFiscalPeriod().getIsClosed()) {
    throw new BusinessException("FISCAL_PERIOD_CLOSED", ...);
}
```

#### Invoice Status Workflow

```
DRAFT → ISSUED → PAID
  ↓       ↓
CANCELED  CANCELED
```

Only `DRAFT` invoices can be edited/deleted.

## Development Workflow

### Phase-Based Development

プロジェクトは4フェーズで開発中（`開発タスク一覧.md` 参照）:

1. **Phase 1:** Backend Foundation (Entity, Repository, DTO, Exception) ✅ Complete
2. **Phase 2:** Backend Business Logic (Service, Controller) ✅ Complete
3. **Phase 3:** Frontend Implementation (React screens) 🚧 In Progress
4. **Phase 4:** Integration & E2E Testing ⏳ Pending

進捗は `tickets/` ディレクトリの各チケットファイルで管理。

### Code Style

- **Backend:** Spring Boot best practices, constructor injection, no `@Autowired`
- **Frontend:** Functional components, Hooks (no class components)
- **Naming:**
  - Entities: `Account.java`, `Journal.java`
  - DTOs: `AccountRequest.java`, `AccountResponse.java`
  - Services: `AccountService.java`
  - Controllers: `AccountController.java`
  - React pages: `AccountList.jsx`, `JournalForm.jsx`

### Git Commit Messages

**全てのコミットメッセージは日本語で記述すること。**

例:
```
✅ Good: "勘定科目マスタのCRUD機能を実装"
❌ Bad:  "Implement account CRUD"
```

## Common Patterns

### Backend: CRUD Service Pattern

```java
@Service
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll(Long companyId) {
        return accountRepository.findByCompanyIdOrderByCode(companyId)
            .stream()
            .map(AccountResponse::from)
            .collect(Collectors.toList());
    }

    public AccountResponse create(Long companyId, AccountRequest request) {
        // Validation
        if (accountRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("コード重複");
        }

        // Create entity
        Account account = new Account();
        account.setCode(request.getCode());
        // ... set other fields

        // Save
        Account saved = accountRepository.save(account);
        return AccountResponse.from(saved);
    }
}
```

### Backend: REST Controller Pattern

```java
@RestController
@RequestMapping("/api/companies/{companyId}/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> findAll(
            @PathVariable Long companyId) {
        List<AccountResponse> accounts = accountService.findAll(companyId);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> create(
            @PathVariable Long companyId,
            @Valid @RequestBody AccountRequest request) {
        AccountResponse created = accountService.create(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(created));
    }
}
```

### Frontend: API Service Pattern

```javascript
// services/accountService.js
import api from '../utils/api';

const accountService = {
  async getAll(companyId) {
    const response = await api.get(`/companies/${companyId}/accounts`);
    return response.data;
  },

  async create(companyId, data) {
    const response = await api.post(`/companies/${companyId}/accounts`, data);
    return response.data;
  },

  async update(companyId, id, data) {
    const response = await api.put(`/companies/${companyId}/accounts/${id}`, data);
    return response.data;
  },

  async delete(companyId, id) {
    await api.delete(`/companies/${companyId}/accounts/${id}`);
  }
};

export default accountService;
```

### Frontend: Modal-Based CRUD Pattern

```javascript
// pages/master/AccountList.jsx
const [showModal, setShowModal] = useState(false);
const [editingAccount, setEditingAccount] = useState(null);

const handleCreate = () => {
  setEditingAccount(null);
  setShowModal(true);
};

const handleEdit = (account) => {
  setEditingAccount(account);
  setShowModal(true);
};

const handleSubmit = async (e) => {
  e.preventDefault();
  try {
    if (editingAccount) {
      await accountService.update(companyId, editingAccount.id, formData);
    } else {
      await accountService.create(companyId, formData);
    }
    setShowModal(false);
    fetchAccounts(); // Refresh list
  } catch (err) {
    alert('保存に失敗: ' + err.message);
  }
};
```

## Database Credentials (Development)

```
Host: localhost:3306
Database: accounting_db
User: accounting_user
Password: accounting_password
Root Password: rootpassword
```

**Note:** これらは開発環境専用。本番環境では環境変数で管理。

## Testing

Backend tests use JUnit 5 + Spring Boot Test.

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=AccountServiceTest

# Run with coverage (if configured)
./mvnw test jacoco:report
```

Frontend testing setup is pending (Phase 4).

## Troubleshooting

### Backend won't start

Check if MySQL is healthy:
```bash
docker-compose ps
docker-compose logs db
```

Verify connection in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://db:3306/accounting_db
```

### Frontend can't reach backend

Check CORS configuration in backend and `VITE_API_URL` in frontend:
```bash
# In docker-compose.yml
VITE_API_URL: http://localhost:8080
CORS_ALLOWED_ORIGINS: http://localhost:3000
```

### JPA/Hibernate errors

Enable SQL logging to debug:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

## Important Notes

1. **No @Autowired on single-constructor classes** - Spring 4.3+ handles it automatically
2. **All commit messages in Japanese** - プロジェクト規約
3. **DTO pattern is mandatory** - Never expose entities in API
4. **Constructor injection over field injection** - 全クラスでコンストラクタインジェクション使用
5. **Modal-based forms for CRUD** - Separate form/list components avoided
