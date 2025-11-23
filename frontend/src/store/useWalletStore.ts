// src/store/useWalletStore.ts
import { create } from 'zustand';
import { getUserIdFromToken } from '../utils/auth';
import request from '../utils/request';

interface Transaction {
  photographerId: string;
  amount: number;
  channel: 'ALIPAY'; // ✅ 补上逗号
  remark: string;
  status: 'PENDING' | 'PROCESSED' | 'SUCCESSED' | 'FALIED';
  createdAt: Date;
}

interface WalletState {
  balance: number;
  transactions: Transaction[];
  alipayBound: boolean;
  alipayAccount: string | null;
  addWithdrawal: (amount: number, description: string) => Promise<void>;
  bindAlipay: (account: string, realName: string) => Promise<void>;
}

export const useWalletStore = create<WalletState>((set) => ({
  balance: 1000,
  transactions: [],
  alipayBound: false,
  alipayAccount: null,

  addWithdrawal: async (amount, remark) => {
    const currentBalance = useWalletStore.getState().balance;
    if (currentBalance < amount) {
      throw new Error('余额不足');
    }

    const userId = getUserIdFromToken();
    // ✅ 确保 userId 存在
    if (!userId) {
      throw new Error('用户未登录');
    }

    const newTx: Transaction = {
      photographerId: userId, // ✅ 现在是 string
      amount,
      channel: 'ALIPAY',
      remark,
      status: 'PENDING',
      createdAt: new Date(),
    };

    try {
      // ✅ await 请求并处理响应
      await request.post('/withdraw', {
        amount,
        remark,
        userId,
      });

      set((state) => ({
        balance: state.balance - amount,
        transactions: [newTx, ...state.transactions],
      }));
    } catch (err) {
      // ✅ 使用 unknown + 类型守卫
      const message = err instanceof Error ? err.message : '提现请求失败';
      console.error(message);
      throw err; // 可让调用方处理
    }
  },

  bindAlipay: async (account, realName) => {
    try {
      await request.post('/alipay/bind-alipay', {
        account,
        realName,
      });
      const masked = account.replace(/^(.{3})(.*)(.{4})$/, '$1****$3');
      set({ alipayBound: true, alipayAccount: masked });
    } catch (err) {
      // ✅ 避免 any
      const message = err instanceof Error ? err.message : '绑定失败';
      console.error(message);
      throw err;
    }
  },
}));