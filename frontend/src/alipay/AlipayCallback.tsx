import React, { useEffect } from 'react';
import { Spin, message } from 'antd';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { bindAlipayWithAuthCode } from '../api/alipay';

const AlipayCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const authCode = searchParams.get('auth_code');
    const state = searchParams.get('state');

    if (!authCode || !state) {
      message.error('授权参数缺失');
      navigate('/profile');
      return;
    }

    bindAlipayWithAuthCode({ authCode, state })
      .then(() => {
        message.success('支付宝绑定成功！');
        navigate('/profile');
      })
      .catch((err) => {
        console.error('绑定失败:', err);
        message.error(err.response?.data?.message || '绑定失败，请重试');
        navigate('/profile');
      });
  }, [searchParams, navigate]);

  return <Spin tip="正在绑定支付宝..." style={{ marginTop: 100, display: 'block' }} />;
};

export default AlipayCallback;