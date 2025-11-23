// src/utils/request.ts
import axios from 'axios';
import type { AxiosResponse } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios'; // ✅ 使用新类型

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081', // 你的后端地址
  timeout: 10000
});

// 请求拦截器：可添加 token、loading 等
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (!(config.data instanceof FormData)) {
      if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
    } }
    // 示例：从 localStorage 获取 token
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`; 
    }
      
    // 可在此处开启 loading 动画
    console.log('请求发出:', config.url);

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：统一处理错误、自动提示等
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 可在此统一处理 code !==200 的业务错误
    const { code, message } = response.data;
    if (code !== 200) {
      console.error('业务错误:', message);
      // 可弹出提示框
      return Promise.reject(new Error(message));
    }
    return response; // 直接返回 data，方便使用
  },
  (error) => {
    // 处理 HTTP 状态码错误（如 401、500）
    const { response } = error;
    if (response) {
      switch (response.status) {
        case 401:
          console.error('未授权，请重新登录');
          // 可跳转到登录页
          break;
        case 500:
          console.error('服务器内部错误');
          break;
        default:
          console.error('请求失败:', response.data?.message || error.message);
      }
    } else {
      console.error('网络错误或请求超时');
    }
    return Promise.reject(error);
  }
);

export default request;