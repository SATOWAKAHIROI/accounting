import api from '../utils/api';

const authService = {
  /**
   * ログイン
   */
  async login(email, password) {
    const response = await api.post('/users/login', { email, password });
    const { data } = response.data;

    if (data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
    }

    return data;
  },

  /**
   * 新規登録
   */
  async register(name, email, password) {
    const response = await api.post('/users', { name, email, password });
    return response.data.data;
  },

  /**
   * ログアウト
   */
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  /**
   * 現在のユーザー取得
   */
  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  /**
   * 認証状態確認
   */
  isAuthenticated() {
    return !!localStorage.getItem('token');
  },
};

export default authService;
