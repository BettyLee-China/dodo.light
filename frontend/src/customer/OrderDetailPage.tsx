// src/pages/OrderDetailPage.jsx
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Descriptions, Space, Tag } from 'antd';
import { useOrderStore } from '../store/orderStore';

const OrderDetailPage = () => {
  const orderId=useParams();
  const navigate = useNavigate();
  const orders = useOrderStore((state) => state.orders);

   // 找到匹配的订单（注意：orderId 在 URL 中是字符串，可能需要类型转换）
  const order = orders.find((item) => String(item.orderId) === String(orderId.orderId));


  // 如果订单为空（比如未设置或跳转错误）
  if (!order) {
    return (
      <Card title="订单详情" style={{ maxWidth: 600, margin: '24px auto' }}>
        <p style={{ textAlign: 'center', color: '#ff4d4f' }}>未找到订单信息</p>
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Button type="primary" onClick={() => navigate('/firstpage')}>
            返回首页
          </Button>
        </div>
      </Card>
    );
  }

  // const formatAmount = (totalAmount:number) => {
  //   return `¥${(totalAmount).toFixed(2)}`; // 假设单位是分
  // };

  const totalAmount=order.quantity*order.unitPrice;
  const renderStatusTag = (orderStatus:string) => {
    const statusMap :Record<string,{text:string;color:string}>= {
        PAID: { text: '已支付', color: 'success' },
        PENDING_PAYMENT: { text: '待支付', color: 'error' },
        CANCELLED: { text: '已取消', color: 'default' },
    };
    const config = statusMap[orderStatus] || { text: orderStatus, color: 'default' };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  const payTime = order.payTime
    ? new Date(order.payTime).toLocaleString('zh-CN')
    : '-';

  return (
    <Card
      title="订单详情"
      style={{ maxWidth: 600, margin: '24px auto' }}
      extra={
        <Button type="link" onClick={() => navigate(-1)}>
          返回
        </Button>
      }
    >
      <Descriptions column={1} bordered size="middle">
        <Descriptions.Item label="订单编号">{order.orderId || '-'}</Descriptions.Item>
        <Descriptions.Item label="商品名称">{order.productName || '-'}</Descriptions.Item>
        <Descriptions.Item label="订单金额">{totalAmount}</Descriptions.Item>
        <Descriptions.Item label="支付状态">{renderStatusTag(order.orderStatus)}</Descriptions.Item>
        <Descriptions.Item label="支付时间">{payTime}</Descriptions.Item>
      </Descriptions>

      <div style={{ textAlign: 'center', marginTop: 24 }}>
        <Space>
          <Button onClick={() => navigate(-1)}>返回上一页</Button>
          {/* 可选：如果需要“联系客服”或“重新支付”按钮，可在此扩展 */}
          {/* {order.orderStatus === 'unpaid' && (
            <Button type="primary" onClick={() => message.info('跳转支付')}>
              重新支付
            </Button>
          )} */}
        </Space>
      </div>
    </Card>
  );
};

export default OrderDetailPage;