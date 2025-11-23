import React, { useState } from 'react';
import { Button, message, Card } from 'antd';
import { getAlipayAuthUrl } from '../api/alipay';
import { useWalletStore } from '../store/useWalletStore';

const BindAlipayForm: React.FC = () => {
  const { alipayBound } = useWalletStore();
  const [loading, setLoading] = useState(false);

  const handleBindClick = async () => {
    setLoading(true);
    try {
      const authUrl = await getAlipayAuthUrl();
      window.location.href = authUrl; // 跳转支付宝
    } catch (error:unknown) {
      message.error('跳转失败，请重试');
      console.log(error);
      setLoading(false);
    }
  };

  if (alipayBound) {
    return (
      <Card title="✅ 支付宝已绑定" style={{ marginBottom: 24 }}>
        <p>提现将自动转入您的支付宝账户。</p>
      </Card>
    );
  }

  return (
    <Card title="🔗 绑定支付宝" style={{ marginBottom: 24 }}>
      <Button
        type="primary"
        onClick={handleBindClick}
        loading={loading}
        block
        size="large"
      >
        前往支付宝授权
      </Button>
      <p style={{ fontSize: 12, color: '#999', marginTop: 8 }}>
        测试时请使用支付宝沙箱买家账号扫码授权
      </p>
    </Card>
  );
};

export default BindAlipayForm;