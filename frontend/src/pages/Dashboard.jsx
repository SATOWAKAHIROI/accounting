import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import FinancialSummary from '../components/FinancialSummary';
import RecentJournals from '../components/RecentJournals';

export default function Dashboard() {
  return (
    <MainLayout>
      <div className="space-y-6">
        <h2 className="text-2xl font-bold text-gray-900">ダッシュボード</h2>

        <FinancialSummary />

        <RecentJournals />
      </div>
    </MainLayout>
  );
}
