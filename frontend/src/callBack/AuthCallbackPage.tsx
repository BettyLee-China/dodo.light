// src/pages/AuthCallbackPage.tsx
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import request from '../utils/request';

export default function AuthCallbackPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const code = searchParams.get('code');
  const provider = searchParams.get('provider') || 'github'; // 可以通过 URL 传 provider

  useEffect(() => {
    if (!code) {
      console.error('缺少授权 code');
      navigate('/login');
      return;
    }

    // 将 code 发送给后端，换取用户登录态
    const exchangeCode = async () => {
      try {
        const response = await request.post('/auth/callback', {
          provider,
          code,
          // 如果你有 userType，也可以传
          userType: localStorage.getItem('pendingUserType') || 'customer', // 可选：记住用户选择的身份
        });

        const { data } = response;
        if (data.code === 200) {
          console.log('第三方登录成功', data);
          // 保存 token（如 JWT）
          localStorage.setItem('token', data.data.token);

          // 跳转到对应页面
          if (data.data.userType === 'photographer') {
            navigate('/photographer/dashboard');
          } else {
            navigate('/customer/home');
          }
        }
      } catch (error) {
        console.error('登录失败:', error);
        navigate('/login');
      }
    };

    exchangeCode();
  }, [code, navigate, provider]);

  return <div>正在登录...</div>;
}