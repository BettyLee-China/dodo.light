// FirstPage.tsx
import { Card, Button, Typography, Flex, InputNumber, message } from 'antd';
import { useEffect, useState } from 'react';
import type { Product } from './types/PhotoData'; // 确保 Product 有 url 字段！
import request from './utils/request';
import { useNavigate } from 'react-router-dom';

const { Meta } = Card;
const { Title } = Typography;

export default function FirstPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState<string | null>(null); // ✅ 改为 string | null
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await request.get('/shop/products');
        const data: Product[] = response.data.data;
        setProducts(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '未知错误');
        message.error('加载商品失败');
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const handleBuyNow = async (product: Product, quantity: number) => {
    if (quantity <= 0) {
      message.warning('请至少选择 1 份');
      return;
    }
    setSubmitting(product.id); // ✅ string
    const url = `/create-order?productId=${product.id}&quantity=${quantity}`;
    console.log(url);
    navigate(url);
  };

  if (loading) {
    return <div style={{ textAlign: 'center', marginTop: 20 }}>加载中...</div>;
  }

  if (error) {
    return <div style={{ color: 'red', textAlign: 'center' }}>错误：{error}</div>;
  }

  return (
    <>
      <Title level={2}>精选摄影作品</Title>
      <Flex wrap="wrap" gap="middle" style={{ marginTop: 20 }}>
        {products.length === 0 ? (
          <p>暂无商品</p>
        ) : (
          products.map((product) => (
            <Card
              key={product.id}
              className="w-60"
              cover={
                <img
                  alt={product.name}
                  src={product.url} // ✅ 确保 Product 有 url
                  style={{ height: 200, objectFit: 'cover' }}
                />
              }
            >
              <Meta
                title={product.name}
                description={<div>价格：¥{product.price}</div>}
              />
              <div style={{ marginTop: 12 }}>
                <Flex align="center" gap="small">
                  <span>数量：</span>
                  <InputNumber
                    min={1}
                    max={99}
                    defaultValue={1}
                    size="small"
                    style={{ width: 80 }}
                    id={`quantity-${product.id}`}
                    // ✅ 移除了 unused 的 onChange
                  />
                  <Button
                    type="primary"
                    size="small"
                    loading={submitting === product.id} // ✅ 类型一致
                    onClick={() => {
                      const input = document.getElementById(
                        `quantity-${product.id}`
                      ) as HTMLInputElement;
                      const quantity = input ? parseInt(input.value, 10) || 1 : 1;
                      handleBuyNow(product, quantity);
                    }}
                  >
                    立即购买
                  </Button>
                </Flex>
              </div>
            </Card>
          ))
        )}
      </Flex>
    </>
  );
}