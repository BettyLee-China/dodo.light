import { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, Radio } from 'antd';
import { useNavigate } from 'react-router-dom';
import request from '../utils/request';
import { v4 as uuidv4 } from 'uuid';
import type { RadioChangeEvent } from 'antd';
import { useAuthStore } from '../store/authStore';
import { getUserIdFromToken } from '../utils/auth';

function LoginPage() {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const auth = useAuthStore();
  const [role, setRole] = useState<'customer' | 'photographer'>('customer'); // ✅ 修复类型

  const [captchaId, setCaptchaId] = useState<string>(uuidv4());
  const [captchaImage, setCaptchaImage] = useState<string>('');

  // ✅ 用 useCallback 包裹，避免 useEffect 依赖问题
  const refreshCaptcha = useCallback(async () => {
    const newCaptchaId = uuidv4();
    setCaptchaId(newCaptchaId);
    try {
      const response = await request.get(`http://localhost:8081/captcha?uuid=${newCaptchaId}`, {
        headers: { 'uuid': newCaptchaId },
      });
      setCaptchaImage(response.data.data);
    } catch (error) {
      console.error('验证码加载失败:', error);
      setCaptchaImage('');
    }
  }, []); // request 和 uuidv4 视为稳定

  useEffect(() => {
    refreshCaptcha();
  }, [refreshCaptcha]); // ✅ 安全依赖

  const GITHUB_CLIENT_ID = 'Ov23liIqURfB4Kqa115E';
  const REDIRECT_URI = 'http://localhost:8081/auth/callback';

  const handleGithubLogin = () => {
    const scope = 'user:email';
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${GITHUB_CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=${scope}`;
    window.location.href = authUrl;
  };

  const handleLogin = async (values: { identifier: string; password: string }) => {
    const data = { username: values.identifier, password: values.password };

    try {
      const response = await request.post("/login", data, {
        headers: {
          'uuid': captchaId,
          'captcha': form.getFieldValue("captcha"),
        },
      });

      if (response.data.code === 200) {
        const token = response.data.data;
        localStorage.setItem('token', token);
        const userId = getUserIdFromToken();

        const authData = {
          username: data.username,
          userId: userId, // 注意：userId 可能为 null，需确保 authStore 允许
          token: token,
          role: role, // ✅ 现在是 'customer' | 'photographer'
        };

        auth.login(authData); // ✅ 类型匹配

        if (role === 'photographer') {
          navigate("/photographer/dashboard");
        } else {
          navigate("/customer/dashboard");
        }
      }
    } catch (error) {
      console.error('登录出错:', error);
    }
  };

  const onChange = (e: RadioChangeEvent) => {
    setRole(e.target.value);
  };

  return (
    <div style={{ maxWidth: 400, margin: 'auto', padding: '40px 20px' }}>
      <div style={{ marginBottom: 24, textAlign: 'center' }}>
        欢迎登录
      </div>
      <Form form={form} layout="vertical" onFinish={handleLogin}>
        <Form.Item>
          <Radio.Group
            block
            optionType="button"
            buttonStyle="solid"
            value={role}
            onChange={onChange}
            options={[
              { value: 'customer', label: '客户' },
              { value: 'photographer', label: '摄影师' },
            ]}
          />
        </Form.Item>
        <Form.Item name="identifier" rules={[{ required: true, message: '请输入账号！' }]}>
          <Input size="large" placeholder="请输入账号" />
        </Form.Item>
        <Form.Item name="password" rules={[{ required: true, message: '请输入密码！' }]}>
          <Input.Password size="large" placeholder="密码" />
        </Form.Item>
        <Form.Item name="captcha" rules={[{ required: true, message: '请输入验证码！' }]}>
          <Input size="large" placeholder="请输入验证码" />
        </Form.Item>
        <Form.Item>
          <img
            src={captchaImage}
            key={captchaId}
            alt="验证码"
            onClick={refreshCaptcha}
            style={{ cursor: 'pointer', width: '100%', height: 40, objectFit: 'contain' }}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" size="large" block>
            登录
          </Button>
        </Form.Item>
        <Form.Item>
          <Button
            type="default"
            size="large"
            block
            icon={<i className="fab fa-github" style={{ marginRight: 8 }}></i>}
            onClick={handleGithubLogin}
          >
            使用 GitHub 登录
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default LoginPage;