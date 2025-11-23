// src/pages/PayPage.tsx
import React, { useEffect, useState } from 'react';
import { Button, Card, message, Spin, Typography, Alert } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import request from '../utils/request'; // 假设你有封装 axios

const { Title, Text } = Typography;

const PayPage: React.FC = () => {
 const {orderId}=useParams();
  const navigate = useNavigate();
  const [orderInfo, setOrderInfo] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [payLoading, setPayLoading] = useState(false);

  // 可选：加载订单信息用于展示
  useEffect(() => {

    const fetchOrder = async () => {
      try {
        const res = await request.get(`/orders/preview/${orderId}`);
        setOrderInfo(res.data.data);
      } catch (err) {
        console.error('加载订单失败', err);
        message.error('订单不存在');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  // 发起支付宝支付（调用你的 /alipay/pay?orderId=xxx）
  const handleAlipay = async () => {
    if (!orderId) return;

    setPayLoading(true);
    try {
      const res = await request.get(`/alipay/pay`,{
        params:{
          orderId:orderId
        }
      });

      const alipayFormHtml: string = res.data.data; // 后端返回的完整 form HTML

      // 创建一个临时 div 来解析 HTML
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = alipayFormHtml.trim();

      // 获取 form 元素
      const form = tempDiv.querySelector('form');
      if (!form) {
        throw new Error('未找到支付宝表单');
      }

      // 设置 form 的 target（可选：_self 当前页跳转）
      form.setAttribute('target', '_self');

      // 将 form 添加到 body 并提交
      document.body.appendChild(form);
      form.submit();

      // 提交后移除（防止内存泄漏）
      document.body.removeChild(form);
    } catch (err: any) {
      console.error('支付宝支付失败', err);
      const msg = err.response?.data?.msg || '支付发起失败，请重试';
      message.error(msg);
      setPayLoading(false);
    }
  };

  if (loading) {
    return <Spin tip="加载订单..." style={{ display: 'block', margin: '50px auto' }} />;
  }

  return (
    <div style={{ maxWidth: 600, margin: '20px auto', padding: 20 }}>
      <Card>
        <Title level={3}>支付宝支付</Title>

        <Alert
          message="沙箱环境"
          description="请使用支付宝沙箱买家账号扫码支付（金额为订单实际金额）"
          type="info"
          showIcon
          style={{ marginBottom: 20 }}
        />

        <p>
          <Text strong>订单号：</Text>
          <Text code>{orderId}</Text>
        </p>
        <p>
          <Text strong>应付金额：</Text>
          <Text strong style={{ color: '#e60000', fontSize: '18px' }}>
            ¥{orderInfo?.totalAmount?.toFixed(2) || '加载中...'}
          </Text>
        </p>

        <Button
          type="primary"
          size="large"
          block
          onClick={handleAlipay}
          loading={payLoading}
          style={{ marginTop: 24, height: 48 }}
        >
          {payLoading ? '正在跳转支付宝...' : '立即支付'}
        </Button>

        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Button type="link" onClick={() => navigate(`/order/${orderId}`)}>
            查看订单详情
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default PayPage;