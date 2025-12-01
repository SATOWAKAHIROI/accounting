import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  HomeIcon,
  DocumentTextIcon,
  UserGroupIcon,
  CurrencyYenIcon,
  DocumentChartBarIcon,
  Cog6ToothIcon,
} from '@heroicons/react/24/outline';

const navigation = [
  { name: 'ダッシュボード', href: '/dashboard', icon: HomeIcon },
  {
    name: 'マスタ管理',
    icon: DocumentTextIcon,
    children: [
      { name: '勘定科目', href: '/master/accounts' },
      { name: '補助科目', href: '/master/sub-accounts' },
      { name: '税区分', href: '/master/tax-types' },
      { name: '取引先', href: '/master/partners' },
      { name: '品目', href: '/master/items' },
    ],
  },
  {
    name: 'トランザクション',
    icon: CurrencyYenIcon,
    children: [
      { name: '仕訳入力', href: '/transaction/journals' },
      { name: '請求書', href: '/transaction/invoices' },
      { name: '入金', href: '/transaction/payments' },
    ],
  },
  {
    name: 'レポート',
    icon: DocumentChartBarIcon,
    children: [
      { name: '総勘定元帳', href: '/report/general-ledger' },
      { name: '試算表', href: '/report/trial-balance' },
      { name: '損益計算書', href: '/report/profit-loss' },
      { name: '貸借対照表', href: '/report/balance-sheet' },
    ],
  },
  {
    name: '設定',
    icon: Cog6ToothIcon,
    children: [
      { name: '会計期間', href: '/settings/fiscal-periods' },
      { name: '会社設定', href: '/settings/company' },
      { name: 'ユーザー管理', href: '/settings/users' },
    ],
  },
];

export default function Sidebar() {
  return (
    <div className="w-64 bg-gray-800 min-h-screen">
      <nav className="mt-5 px-2">
        {navigation.map((item) => (
          <div key={item.name}>
            {!item.children ? (
              <NavLink
                to={item.href}
                className={({ isActive }) =>
                  `group flex items-center px-2 py-2 text-sm font-medium rounded-md ${
                    isActive
                      ? 'bg-gray-900 text-white'
                      : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                  }`
                }
              >
                <item.icon className="mr-3 h-6 w-6" />
                {item.name}
              </NavLink>
            ) : (
              <div className="space-y-1">
                <div className="flex items-center px-2 py-2 text-sm font-medium text-gray-300">
                  <item.icon className="mr-3 h-6 w-6" />
                  {item.name}
                </div>
                <div className="pl-11 space-y-1">
                  {item.children.map((child) => (
                    <NavLink
                      key={child.name}
                      to={child.href}
                      className={({ isActive }) =>
                        `group flex items-center px-2 py-2 text-sm font-medium rounded-md ${
                          isActive
                            ? 'bg-gray-900 text-white'
                            : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                        }`
                      }
                    >
                      {child.name}
                    </NavLink>
                  ))}
                </div>
              </div>
            )}
          </div>
        ))}
      </nav>
    </div>
  );
}
