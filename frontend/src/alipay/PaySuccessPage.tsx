// src/pages/PaySuccessPage.tsx
import React, { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Result, Button, Spin, Alert } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import { useOrderStore } from '../store/orderStore';

const PaySuccessPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const orderId = searchParams.get('out_trade_no');
  console.log(orderId)
  const { orders, loading, error, fetchOrderById,currentOrder } = useOrderStore();

 
  // 加载订单状态
  useEffect(() => {
    if (!orderId) return;
    fetchOrderById(orderId);
  }, [orderId, fetchOrderById]);

  // 处理返回首页
  const handleBackHome = () => {
    navigate('/');
  };

  // 渲染加载中
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 20px' }}>
        <Spin indicator={<LoadingOutlined style={{ fontSize: 48 }} spin />} />
        <p style={{ marginTop: 24, fontSize: 16 }}>正在验证支付结果...</p>
      </div>
    );
  }


  // 渲染错误
  if (error || !orders) {
    return (
      <div style={{ padding: '50px 20px' }}>
        <Result
          status="error"
          title="支付结果验证失败"
          subTitle={error || '未找到有效订单，请勿手动访问此页面'}
          extra={
            <Button type="primary" onClick={handleBackHome}>
              返回首页
            </Button>
          }
        />
      </div>
    );
  }
 

  if (!currentOrder) {
    return <div>未找到订单信息</div>;
  }

  // 判断是否真正支付成功
  const isPaid = currentOrder.orderStatus === 'PAID' ; 
  console.log(isPaid);
  const totalAmount=currentOrder.quantity*currentOrder.unitPrice;

  if (!isPaid) {
    return (
      <div style={{ padding: '50px 20px' }}>
        <Alert
          message="支付未完成"
          description={`当前订单状态：${currentOrder.orderStatus}。请稍后在“我的订单”中查看最新状态。`}
          type="warning"
          showIcon
          action={
            <Button size="small" type="primary" onClick={() => fetchOrderById(currentOrder?.orderId)}>
              刷新状态
            </Button>
          }
        />
        <div style={{ textAlign: 'center', marginTop: 24 }}>
          <Button onClick={handleBackHome}>返回首页</Button>
        </div>
      </div>
    );
  }

  // 渲染支付成功
  return (
    <div style={{ padding: '50px 20px' }}>
      <Result
        status="success"
        title="支付成功！"
        subTitle={`订单号：${currentOrder.orderId}`}
        extra={[
          <div key="amount" style={{ marginBottom: 16, fontSize: 16 }}>
            支付金额：<strong>¥{totalAmount?.toFixed(2)}</strong>
          </div>,
          <Button type="primary" key="back" onClick={handleBackHome}>
            返回首页
          </Button>,
          <Button key="order" onClick={() => navigate(`/order/${orderId}`)}>
            查看我的订单
          </Button>,
        ]}
      />
    </div>
  );
};

export default PaySuccessPage;