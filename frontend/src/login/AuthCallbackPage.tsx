
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import request from '../utils/request';

export default function AuthCallbackPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const code = searchParams.get('code');

  useEffect(() => {
    if (!code) {
      alert('授权失败');
      navigate('/login');
      return;
    }

    // 发送 code 给后端
    const loginWithGithub = async () => {
      try {
        const response = await request.post('/auth/github/callback', {
          code,
          userType: localStorage.getItem('pendingUserType') || 'customer', // 可选：记住用户身份
        });

        const { data } = response;
        if (data.code === 200) {
          localStorage.setItem('token', data.data.token);
          navigate(data.data.userType === 'photographer' 
            ? '/photographer/dashboard' 
            : '/customer/home'
          );
        }
      } catch (error) {
        console.error('GitHub登录失败:', error);
        navigate('/login');
      }
    };

    loginWithGithub();
  }, [code, navigate]);

  return <div>正在登录 GitHub...</div>;
}