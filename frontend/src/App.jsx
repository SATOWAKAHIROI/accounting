import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/common/ProtectedRoute";

// Auth pages
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";

// Dashboard
import Dashboard from "./pages/Dashboard";

// Master pages
import AccountList from "./pages/master/AccountList";
import SubAccountList from "./pages/master/SubAccountList";
import TaxTypeList from "./pages/master/TaxTypeList";
import PartnerList from "./pages/master/PartnerList";
import ItemList from "./pages/master/ItemList";

// Transaction pages
import JournalList from "./pages/transaction/JournalList";
import InvoiceList from "./pages/transaction/InvoiceList";
import PaymentList from "./pages/transaction/PaymentList";

// Report pages
import GeneralLedger from "./pages/report/GeneralLedger";
import TrialBalance from "./pages/report/TrialBalance";
import ProfitLoss from "./pages/report/ProfitLoss";
import BalanceSheet from "./pages/report/BalanceSheet";

// Settings pages
import FiscalPeriodList from "./pages/settings/FiscalPeriodList";
import CompanySettings from "./pages/settings/CompanySettings";
import UserList from "./pages/settings/UserList";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          {/* Master management routes */}
          <Route
            path="/master/accounts"
            element={
              <ProtectedRoute>
                <AccountList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/master/sub-accounts"
            element={
              <ProtectedRoute>
                <SubAccountList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/master/tax-types"
            element={
              <ProtectedRoute>
                <TaxTypeList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/master/partners"
            element={
              <ProtectedRoute>
                <PartnerList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/master/items"
            element={
              <ProtectedRoute>
                <ItemList />
              </ProtectedRoute>
            }
          />

          {/* Transaction routes */}
          <Route
            path="/transaction/journals"
            element={
              <ProtectedRoute>
                <JournalList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/transaction/invoices"
            element={
              <ProtectedRoute>
                <InvoiceList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/transaction/payments"
            element={
              <ProtectedRoute>
                <PaymentList />
              </ProtectedRoute>
            }
          />

          {/* Report routes */}
          <Route
            path="/report/general-ledger"
            element={
              <ProtectedRoute>
                <GeneralLedger />
              </ProtectedRoute>
            }
          />
          <Route
            path="/report/trial-balance"
            element={
              <ProtectedRoute>
                <TrialBalance />
              </ProtectedRoute>
            }
          />
          <Route
            path="/report/profit-loss"
            element={
              <ProtectedRoute>
                <ProfitLoss />
              </ProtectedRoute>
            }
          />
          <Route
            path="/report/balance-sheet"
            element={
              <ProtectedRoute>
                <BalanceSheet />
              </ProtectedRoute>
            }
          />

          {/* Settings routes */}
          <Route
            path="/settings/fiscal-periods"
            element={
              <ProtectedRoute>
                <FiscalPeriodList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/settings/company"
            element={
              <ProtectedRoute>
                <CompanySettings />
              </ProtectedRoute>
            }
          />
          <Route
            path="/settings/users"
            element={
              <ProtectedRoute>
                <UserList />
              </ProtectedRoute>
            }
          />

          {/* Default redirect */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
