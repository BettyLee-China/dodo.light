// pages/CreateOrder.tsx
import React, { useEffect, useState } from 'react';
import { Button, Card, Typography, Spin, message } from 'antd';
import { useNavigate, useSearchParams } from 'react-router-dom';
import request from '../utils/request';
import type { Product } from '../types/PhotoData';
import AddressSelector from '../components/AddressSelector';
import useAddressStore from '../store/addressStore';

const { Title, Text } = Typography;

const CreateOrder: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const productId = searchParams.get('productId');
  const quantityStr = searchParams.get('quantity');

  const [product, setProduct] = useState<Product | null>(null);
  const [quantity, setQuantity] = useState<number>(1);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
   const { addresses, selectedAddressId } = useAddressStore();

  // 从 URL 解析参数并加载商品
  useEffect(() => {
    if (!productId) {
      message.error('缺少商品信息');
      return;
    }

    const qty = parseInt(quantityStr || '1', 10);
    if (isNaN(qty) || qty < 1) {
      setQuantity(1);
    } else {
      setQuantity(qty);
    }

    // 加载商品详情
    const fetchProduct = async () => {
      try {
        const res = await request.get(`/shop/detail/${productId}`);
        setProduct(res.data.data);
      } catch (err) {
        console.error('加载商品失败', err);
        message.error('商品信息加载失败');
        navigate('/products', { replace: true });
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId, quantityStr, navigate]);

  // 提交订单
  const handleSubmitOrder = async () => {
    if (!product) return;

    setSubmitting(true);
    try {
       const orderData = {
            addressId: selectedAddressId,
            productId: productId,
            quantity: quantity,
      };

      const res = await request.post('/orders/createOne', orderData);
      const orderId = res.data.data.orderId;

      message.success('订单创建成功！');
      navigate(`/pay/${orderId}`);
    } catch (err) {
      console.error('创建订单失败', err);
      message.error('创建订单失败，请重试');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', marginTop: 40 }}>
        <Spin size="large" />
        <p>加载订单信息...</p>
      </div>
    );
  }

  if (!product) {
    return null; // 已跳转
  }

  const totalAmount = (product.price * quantity).toFixed(2);

  return (
    <div style={{ maxWidth: 600, margin: '20px auto', padding: 20 }}>
      <Title level={3}>确认订单</Title>

       {/* 收货地址 */}
      <Card title="收货地址" size="small" style={{ marginBottom: 20 }}>
        <AddressSelector/>
        {addresses
          .filter(addr => addr.id === selectedAddressId)
          .map(addr => (
            <div key={addr.id}>
              <Text strong>{addr.receiverName}</Text> {addr.phone}
              <br />
              <Text type="secondary">{addr.detailAddress}</Text>
            </div>
          ))}
        {/* 实际项目中这里应有“管理地址”链接 */}
      </Card>

      {/* 商品信息 */}
      <Card size="small">
        <div style={{ display: 'flex', gap: 16 }}>
          <img
            src={product.url}
            alt={product.name}
            style={{ width: 80, height: 80, objectFit: 'cover', borderRadius: 4 }}
          />
          <div style={{ flex: 1 }}>
            <Text strong>{product.name}</Text>
            <div style={{ marginTop: 8 }}>
              <Text type="secondary">单价：</Text>
              <Text>¥{product.price}</Text>
            </div>
            <div>
              <Text type="secondary">数量：</Text>
              <Text>{quantity} 份</Text>
            </div>
            <div style={{ marginTop: 8 }}>
              <Text type="danger" strong>
                总计：¥{totalAmount}
              </Text>
            </div>
          </div>
        </div>
      </Card>

      {/* 提交按钮 */}
      <div style={{ textAlign: 'right', marginTop: 24 }}>
        <Button onClick={() => navigate(-1)} style={{ marginRight: 12 }}>
          返回修改
        </Button>
        <Button
          type="primary"
          size="large"
          onClick={handleSubmitOrder}
          loading={submitting}
        >
          立即支付 ¥{totalAmount}
        </Button>
      </div>
    </div>
  );
};

export default CreateOrder;