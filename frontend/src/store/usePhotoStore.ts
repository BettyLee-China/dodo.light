// src/store/usePhotoStore.ts
import { create } from 'zustand';
import request from '../utils/request';
import type { Photo } from '../types/Photo';

interface PhotoStore {
  photos: Photo[];
  loading: boolean;
  fetchPhotos: (photographerId: string) => Promise<void>; // 👈 统一为 string
  deletePhoto: (id: string) => Promise<void>;
  deletePhotos: (ids: string[]) => Promise<void>;
}

const usePhotoStore = create<PhotoStore>((set) => ({
  photos: [],
  loading: false,

  // 获取作品集 —— photographerId 应为 string
  fetchPhotos: async (photographerId: string) => {
    set({ loading: true });
    try {
      const response = await request.get(`/photographer/portfolios/${photographerId}`);
      // 假设 response.data.data 是 Photo[] 类型
      set({ photos: response.data.data, loading: false });
    } catch (error) {
      console.error('获取作品集失败:', error);
      set({ loading: false });
    }
  },

  // 删除一张图片
  deletePhoto: async (id: string) => {
    try {
      await request.delete(`/photographer/deletePhoto/${id}`);
      set((state) => ({
        photos: state.photos.filter((photo) => photo.id !== id), // ✅ photo 自动推断为 Photo
      }));
    } catch (error) {
      console.error('删除图片失败:', error);
    }
  },

  // 批量删除图片
  deletePhotos: async (ids: string[]) => {
    try {
      await request.delete(`/photographer/deletePhotos`, { data: { ids } });
      set((state) => ({
        photos: state.photos.filter((photo) => !ids.includes(photo.id)), // ✅ 同上
      }));
    } catch (error) {
      console.error('批量删除图片失败:', error);
    }
  },
}));

export default usePhotoStore;