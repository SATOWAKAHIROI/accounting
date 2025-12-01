import api from '../utils/api';

const COMPANY_ID = 1; // TODO: Get from auth context

const reportService = {
  async getGeneralLedger(accountId, startDate, endDate) {
    const response = await api.get(`/companies/${COMPANY_ID}/reports/general-ledger`, {
      params: { accountId, startDate, endDate }
    });
    return response.data.data;
  },

  async getTrialBalance(asOfDate) {
    const response = await api.get(`/companies/${COMPANY_ID}/reports/trial-balance`, {
      params: { asOfDate }
    });
    return response.data.data;
  },

  async getProfitLoss(startDate, endDate) {
    const response = await api.get(`/companies/${COMPANY_ID}/reports/profit-loss`, {
      params: { startDate, endDate }
    });
    return response.data.data;
  },

  async getBalanceSheet(asOfDate) {
    const response = await api.get(`/companies/${COMPANY_ID}/reports/balance-sheet`, {
      params: { asOfDate }
    });
    return response.data.data;
  },
};

export default reportService;
