// src/store/orderStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import request from '../utils/request';
import { getUserIdFromToken } from '../utils/auth';
import type { OrderVO } from '../interfaces/order';

interface OrderState {
  orders: OrderVO[];               // 订单列表（持久化）
  currentOrder: OrderVO | null;    // 当前订单详情
  loading: boolean;
  error: string | null;

  getOrders: () => Promise<void>;
  fetchOrderById: (orderId: string) => Promise<void>;
  clearOrders: () => void;
}

export const useOrderStore = create<OrderState>()(
  persist(
    (set) => ({
      orders: [],
      currentOrder: null,
      loading: false,
      error: null,

      // 获取订单列表
      getOrders: async () => {
        set({ loading: true, error: null });
        try {
          const userId = getUserIdFromToken();
          const res = await request.get(`/orders/get/${userId}`);
          const data = res.data.data;
          set({ 
            orders: Array.isArray(data) ? data : [], 
            loading: false 
          });
        } catch (err) {
          // ✅ 使用 unknown + 类型守卫
          const message = err instanceof Error ? err.message : '加载失败';
          set({ orders: [], error: message, loading: false });
        }
      },

      // 获取单个订单详情
      fetchOrderById: async (orderId: string) => {
        set({ loading: true, error: null });
        try {
          const res = await request.get(`/orders/${orderId}/status`);
          const orderDetail = res.data.data;
          set({ 
            currentOrder: orderDetail, 
            loading: false 
          });
        } catch (err) {
          // ✅ 同上
          const message = err instanceof Error ? err.message : '加载失败';
          set({ currentOrder: null, error: message, loading: false });
        }
      },

      clearOrders: () => set({ orders: [] }),
    }),
    {
      name: 'order-store',
      partialize: (state) => ({ orders: state.orders }),
    }
  )
);