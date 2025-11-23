// src/customer/OrderPage.tsx
import { useEffect } from 'react';
import { Card, Typography, List, Tag, Divider, Image } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useOrderStore } from '../store/orderStore';
import type { OrderVO } from '../interfaces/order';

const { Title, Text } = Typography;

// 状态标签映射 —— 添加索引签名避免 TS 错误（虽然这里没报，但推荐）
const statusMap: Record<string, { text: string; color: string }> = {
  PAID: { text: '已完成', color: 'green' },
  PENDING_PAYMENT: { text: '待处理', color: 'orange' },
  CANCELLED: { text: '已取消', color: 'red' },
};

export default function OrderPage() {
  const navigate = useNavigate();
  const getOrders = useOrderStore((state) => state.getOrders);
  const orders = useOrderStore((state) => state.orders); // 建议变量名用复数 orders

  // 从后端加载订单
  useEffect(() => {
    getOrders();
  }, [getOrders]); // ✅ 添加 getOrders 到依赖数组

  // 计算单笔订单总价
  const getTotal = (price: number, quantity: number) => {
    return (price * quantity).toFixed(2);
  };

  return (
    <div style={{ maxWidth: 800, margin: '20px auto', padding: '0 20px' }}>
      <Card>
        <Title level={3}>我的订单</Title>
        <Text type="secondary">查看您的购买记录</Text>
        <Divider />

        <List
          dataSource={orders}
          renderItem={(order: OrderVO) => (
            <List.Item
              key={order.orderId}
              onClick={() => navigate(`/order/${order.orderId}`)}
              style={{ cursor: 'pointer' }}
            >
              <div style={{ width: '100%' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                  <Text strong>订单 #{order.orderId}</Text>
                  <Tag color={statusMap[order.orderStatus]?.color || 'default'}>
                    {statusMap[order.orderStatus]?.text || order.orderStatus}
                  </Tag>
                </div>

                <div style={{ display: 'flex', gap: 16, alignItems: 'center', marginBottom: 8 }}>
                  <Image
                    src={order.imageUrl}
                    alt="图片"
                    width={60}
                    height={60}
                    style={{ objectFit: 'cover', borderRadius: 4 }}
                    preview={false}
                  />
                  <Text>{order.productName}</Text>
                  <Text>¥{order.unitPrice} × {order.quantity}</Text>
                  <Text strong>总计：¥{getTotal(order.unitPrice, order.quantity)}</Text>
                </div>

                {/* ✅ 修复：将 Date 转为字符串 */}
                <div style={{ fontSize: 12, color: '#999' }}>
                  下单时间：
                  {order.createTime instanceof Date
                    ? order.createTime.toLocaleString('zh-CN')
                    : String(order.createTime)} {/* 兜底：如果已经是字符串 */}
                </div>
              </div>
            </List.Item>
          )}
        />

        {Array.isArray(orders) && orders.length === 0 && (
          <div style={{ textAlign: 'center', color: '#999', marginTop: 20 }}>
            <p>暂无订单</p>
            <button
              onClick={() => navigate('/firstpage')}
              style={{
                border: 'none',
                background: '#1890ff',
                color: 'white',
                padding: '8px 16px',
                borderRadius: 4,
                cursor: 'pointer',
              }}
            >
              去逛逛
            </button>
          </div>
        )}
      </Card>
    </div>
  );
}