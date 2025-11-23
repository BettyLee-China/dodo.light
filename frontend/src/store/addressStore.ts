// store/addressStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import request from '../utils/request';
import { getUserIdFromToken } from '../utils/auth';
import type { AddressVO } from '../types/address';

interface AddressState {
  addresses: AddressVO[];
  selectedAddressId: number | null;
  fetchAddresses: () => Promise<void>;
  addAddress: (address: Omit<AddressVO, 'id' | 'userId' | 'createTime' | 'updateTime'>) => Promise<void>;
  updateAddress: (id: number, address: Partial<AddressVO>) => Promise<void>;
  deleteAddress: (id: number) => Promise<void>;
  selectAddress: (id: number) => void;
  getDefaultAddress: () => AddressVO | undefined;
  clearAddress:()=>void;
}

const useAddressStore = create<AddressState>()(
  persist(
    (set, get) => ({
      addresses: [],
      selectedAddressId: null,

      // 拉取地址列表
      fetchAddresses: async () => {
        try {
          const userId = getUserIdFromToken();
          const res = await request.get(`/address/${userId}`);
          const addresses: AddressVO[] = res.data.data || [];
          set({ addresses });
          // 如果没有选中地址，自动选默认地址或第一个
          const { selectedAddressId } = get();
          if (selectedAddressId === null && addresses.length > 0) {
            const defaultAddr = addresses.find(a => a.isDefault);
            set({ selectedAddressId: defaultAddr?.id || addresses[0].id });
          }
        } catch (err) {
          console.error('获取地址失败', err);
        }
      },

      // 添加地址
      addAddress: async (newAddress) => {
        const userId = getUserIdFromToken();
        const res = await request.post('/address/add',{
             ...newAddress, userId 
        },
    {headers:{ 'Content-Type': 'application/json'}});
        const savedAddress: AddressVO = res.data.data;
        set((state) => ({
          addresses: [...state.addresses, savedAddress],
          selectedAddressId: savedAddress.id, // 自动选中新地址
        }));
      },

      // 更新地址
      updateAddress: async (id, updates) => {
        await request.post(`/address/update/${id}`, updates);
        set((state) => ({
          addresses: state.addresses.map(addr =>
            addr.id === id ? { ...addr, ...updates } : addr
          ),
        }));
      },

      // 删除地址
      deleteAddress: async (id) => {
        await request.delete(`/address/delete/${id}`);
        set((state) => {
          const newAddresses = state.addresses.filter(a => a.id !== id);
          let newSelectedId = state.selectedAddressId;
          if (newSelectedId === id) {
            newSelectedId = newAddresses.find(a => a.isDefault)?.id || newAddresses[0]?.id || null;
          }
          return { addresses: newAddresses, selectedAddressId: newSelectedId };
        });
      },

      // 选择地址
      selectAddress: (id) => {
        set({ selectedAddressId: id });
      },

      // 获取默认地址
      getDefaultAddress: () => {
        return get().addresses.find(a => a.isDefault);
      },

      clearAddress:()=>{
        set({selectedAddressId:null,addresses:[]});
      }
    }),
    {
      name: 'address-storage',
      partialize: (state) => ({ addresses: state.addresses, selectedAddressId: state.selectedAddressId }),
    }
  )
);

export default useAddressStore;