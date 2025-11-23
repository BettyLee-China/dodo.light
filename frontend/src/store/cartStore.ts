// src/store/cartStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import request from '../utils/request';
import { getUserIdFromToken } from '../utils/auth';

// 修正拼写：brirf → brief
export interface ProductVO {
  id: number;
  name: string;
  price: number;
  url: string;
  brief: string; // ✅ 修正拼写
  stock: number;
  originalPrice: number;
  status: number;
  createTime: Date;
  updateTime: Date;
  merchant: {
    photographerId: number;
    nickname: string;
    avatar: string;
  };
}

export interface CartItem extends ProductVO {
  quantity: number;
}

interface CartState {
  items: CartItem[];
  addItem: (product: ProductVO) => Promise<void>;
  initFromServer: () => Promise<void>;
  removeItem: (id: number) => Promise<void>; // ✅ 返回 Promise
  deleteItem: (id: number) => Promise<void>;
  batchDeleteItems: (productIds: number[]) => Promise<void>; // ✅ 参数名改为 productIds
  clearCart: () => Promise<void>;
  getTotalQuantity: () => number;
  getTotalPrice: () => number;
  updateQuantity: (id: number, quantity: number) => Promise<void>;
}

const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: async (product: ProductVO) => {
        const currentItems = get().items;
        const existingItem = currentItems.find((item) => item.id === product.id);

        let newItems: CartItem[];
        if (existingItem) {
          newItems = currentItems.map((item) =>
            item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
          );
          try {
            await request.post('/carts/update', {
              productId: product.id,
              userId: getUserIdFromToken(),
              quantity: existingItem.quantity + 1,
            });
          } catch (err) {
            console.error('更新购物车失败', err);
          }
        } else {
          newItems = [...currentItems, { ...product, quantity: 1 }];
          try {
            await request.post('/carts/add', {
              productId: product.id,
              userId: getUserIdFromToken(),
              quantity: 1,
            });
          } catch (err) {
            console.error('添加到购物车失败', err);
          }
        }
        set({ items: newItems });
      },

      initFromServer: async () => {
        try {
          const userId = getUserIdFromToken();
          if (!userId) return;
          const response = await request.get(`/carts/${userId}`);
          // 假设后端返回格式: { items: { "1": { id: 1, ..., quantity: 2 }, "2": ... } }
          const cartItems = Object.entries(response.data.data.items)
            .filter(([, item]) => item != null)
            .map(([id, item]) => {
              // ✅ 确保 item 是对象后再展开
              const typedItem = item as Omit<CartItem, 'id'>;
              return { id: Number(id), ...typedItem };
            });

          set({ items: cartItems });
        } catch (err) {
          console.error('拉取购物车失败', err);
        }
      },

      removeItem: async (id: number) => {
        const currentItems = get().items;
        const itemToUpdate = currentItems.find((item) => item.id === id);
        if (!itemToUpdate) return;

        let newItems: CartItem[];
        if (itemToUpdate.quantity > 1) {
          newItems = currentItems.map((item) =>
            item.id === id ? { ...item, quantity: item.quantity - 1 } : item
          );
          try {
            await request.post('/carts/update', {
              productId: id,
              userId: getUserIdFromToken(),
              quantity: itemToUpdate.quantity - 1,
            });
          } catch (err) {
            console.error('更新购物车数量失败', err);
            return;
          }
        } else {
          newItems = currentItems.filter((item) => item.id !== id);
          try {
            await request.delete(`/carts/remove`, {
              data: { productId: id, userId: getUserIdFromToken() },
            });
          } catch (err) {
            console.error('删除购物车项失败', err);
            return;
          }
        }
        set({ items: newItems });
      },

      deleteItem: async (id: number) => {
        const currentItems = get().items;
        const updatedItems = currentItems.filter((item) => item.id !== id);
        set({ items: updatedItems });

        try {
          await request.delete(`/carts/remove`, {
            data: { productId: id, userId: getUserIdFromToken() },
          });
        } catch (err) {
          console.error('删除购物车项失败', err);
          set({ items: currentItems }); // 回滚
        }
      },

      batchDeleteItems: async (productIds: number[]) => {
        const currentItems = get().items;
        const updatedItems = currentItems.filter((item) => !productIds.includes(item.id));
        set({ items: updatedItems });

        try {
          await request.post('/carts/delete', {
            productIds,
            userId: getUserIdFromToken(),
          });
        } catch (err) {
          console.error('批量删除购物车项失败', err);
          set({ items: currentItems }); // 回滚
        }
      },

      clearCart: async () => {
        set({ items: [] });
        try {
          await request.delete('/carts/clear', {
            data: { userId: getUserIdFromToken() },
          });
        } catch (err) {
          console.error('清空购物车失败', err);
        }
      },

      getTotalQuantity: () => {
        return get().items.reduce((sum, item) => sum + item.quantity, 0);
      },

      getTotalPrice: () => {
        return get().items.reduce((sum, item) => sum + item.price * item.quantity, 0);
      },

      updateQuantity: async (id: number, quantity: number) => {
        if (quantity <= 0) {
          // 如果数量 ≤ 0，直接删除
          get().deleteItem(id);
          return;
        }

        const currentItems = get().items;
        const item = currentItems.find((i) => i.id === id);
        if (!item) return;

        const newItems = currentItems.map((i) => (i.id === id ? { ...i, quantity } : i));
        set({ items: newItems });

        try {
          await request.post('/carts/update', {
            productId: id,
            userId: getUserIdFromToken(),
            quantity,
          });
        } catch (err) {
          console.error('更新购物车数量失败', err);
          set({ items: currentItems }); // 回滚
        }
      },
    }),
    {
      name: 'cart-storage',
      partialize: (state) => ({ items: state.items }),
    }
  )
);

export default useCartStore;