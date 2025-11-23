// store/userProfileStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import request from '../utils/request';
import { getUserIdFromToken } from '../utils/auth';

// ✅ 替代 enum：使用 const 断言对象
export const GenderEnum = {
  MALE: '男',
  FEMALE: '女',
} as const;

export type Gender = typeof GenderEnum[keyof typeof GenderEnum];

// ✅ 接口定义
export interface UserProfile {
  userId: number | string | null;
  nickname: string;
  avatar: string | null;
  bio: string;
  gender: Gender | null;
  birthday: string | null;
  age: number | string;
  loading: boolean;
  error: string | null;

  getProfile: (userId: number | string) => Promise<void>;
  modifyProfile: (userId: number | string, formData: FormData) => Promise<void>;
  uploadAvatar: (userId: string , file: File) => Promise<void>;
  clearProfile: () => void;
}

export const useUserProfileStore = create<UserProfile>()(
  persist(
    (set, get) => ({
      userId: null,
      nickname: '',
      avatar: null,
      bio: '这里是一片荒漠',
      gender: GenderEnum.FEMALE,
      birthday: null,
      age: '',
      loading: false,
      error: null,

      getProfile: async (userId) => {
        set({ loading: true, error: null });
        try {
          const response = await request.get(`/profile/${userId}`);
          const data = response.data.data;

          set({
            userId: getUserIdFromToken(),
            nickname: data.nickname || '',
            avatar: data.avatar || null,
            bio: data.bio || '这里是一片荒漠',
            gender: data.gender || null,
            birthday: data.birthday || null,
            loading: false,
          });
        } catch (err: unknown) {
          let errorMessage = '加载失败';
          if (err instanceof Error) {
            errorMessage = err.message;
          } else if (typeof err === 'string') {
            errorMessage = err;
          }
          set({ error: errorMessage, loading: false });
        }
      },

      modifyProfile: async (userId, formData) => {
        set({ loading: true, error: null });
        try {
          const response = await request.put(`/profile/modify/${userId}`, formData);
          const data = response.data.data;
          

          set({
            userId,
            nickname: data.nickname || get().nickname,
            avatar: data.avatar || get().avatar,
            bio: data.bio || get().bio,
            gender: data.gender || get().gender,
            birthday: data.birthday || get().birthday,
            loading: false,
          });
        } catch (err: unknown) {
          let errorMessage = '更新失败';
          if (err instanceof Error) {
            errorMessage = err.message;
          } else if (typeof err === 'string') {
            errorMessage = err;
          }
          set({ error: errorMessage, loading: false });
        }
      },

      uploadAvatar: async (userId, file) => {
        const formData = new FormData();
        formData.append('avatar', file);
        try {
          const response = await request.post(`/profile/${userId}/upload-avatar`, formData);
          const data = response.data.data;
          set({ avatar: data });
        } catch (err: unknown) {
          let errorMessage = '头像上传失败';
          if (err instanceof Error) {
            errorMessage = err.message;
          }
          set({ error: errorMessage });
        }
      },

      clearProfile: () => {
        set({
          userId: null,
          nickname: '',
          avatar: null,
          bio: '这里是一片荒漠',
          gender: GenderEnum.FEMALE,
          birthday: null,
          age: '',
          loading: false,
          error: null,
        });
      },
    }),
    {
      name: 'user-profile-storage',
    }
  )
);