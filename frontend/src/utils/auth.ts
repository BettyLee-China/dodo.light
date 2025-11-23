import { jwtDecode } from "jwt-decode";

// 保存 token
export const saveToken = (token:string) => {
  localStorage.setItem('token', token);
};

// 获取 token
export const getToken = () => {
  return localStorage.getItem('token');
};

// 删除 token
export const removeToken = () => {
  localStorage.removeItem('token');
};

// 保存 userId
export const saveUserId = (userId:string) => {
  localStorage.setItem('userId', userId);
};

// 获取 userId
export const getUserId = () => {
  return localStorage.getItem('userId');
};

// 删除 userId
export const removeUserId = () => {
  localStorage.removeItem('userId');
};

// 从 token 中直接解析 userId（即使页面刷新，只要 token 还在，就能解析）
export const getUserIdFromToken = () => {
  const token = getToken();
  if (!token) return null;
  try {
    const decoded = jwtDecode(token);
    return decoded.sub;
  } catch (error) {
    console.error('Invalid token', error);
    return null;
  }
};