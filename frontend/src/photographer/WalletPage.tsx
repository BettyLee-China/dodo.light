// src/components/WalletPage.tsx
import React, { useState } from 'react';
import { Button, Card, List, Typography, InputNumber, Space, message } from 'antd';
import { MinusOutlined } from '@ant-design/icons';
import { useWalletStore } from '../store/useWalletStore';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

const WalletPage: React.FC = () => {
  const { balance, transactions, addWithdrawal } = useWalletStore();
  const [withdrawalAmount, setWithdrawalAmount] = useState<number>(50);
  const navigate = useNavigate();

  const handleAddWithdrawal = () => {
    if (withdrawalAmount <= 0) {
      message.warning('金额必须大于0');
      return;
    }
    if (withdrawalAmount > balance) {
      message.error('余额不足');
      return;
    }
    // 假设 addWithdrawal 会创建一条 amount 为负数的记录
    addWithdrawal(withdrawalAmount, 'withdrawal'); // 第二个参数可能被忽略，但保留以兼容函数签名
    message.success('提现成功');
  };

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <Title level={2}>我的钱包</Title>
      <Button onClick={() => navigate('/alipay/bind')} style={{ marginBottom: '16px' }}>
        绑定支付宝账户
      </Button>

      {/* 余额卡片 */}
      <Card style={{ marginBottom: '24px' }}>
        <Title level={3} style={{ margin: 0 }}>
          余额: <Text type="success">¥{balance.toFixed(2)}</Text>
        </Title>
      </Card>

      {/* 操作区域 */}
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Card title="新增提现">
          <Space>
            <InputNumber
              min={0.01}
              precision={2}
              value={withdrawalAmount}
              onChange={(v) => v !== null && setWithdrawalAmount(v)}
              placeholder="金额"
            />
            <Button danger icon={<MinusOutlined />} onClick={handleAddWithdrawal}>
              提现
            </Button>
          </Space>
        </Card>
      </Space>

      {/* 交易记录 —— 仅使用实际存在的字段 */}
      <Card title="交易记录" style={{ marginTop: '24px' }}>
        <List
          dataSource={transactions}
          renderItem={(tx) => {
            // 假设：正数为收入，负数为支出（提现）
            const isWithdrawal = tx.amount < 0;
            const displayAmount = Math.abs(tx.amount).toFixed(2);
            const description = isWithdrawal ? '提现' : '收入';

            return (
              <List.Item>
                <List.Item.Meta
                  title={description}
                  // 无法显示时间，因为没有 timestamp/createdAt
                  description={`ID: ${tx.photographerId.substring(0, 8)}...`}
                />
                <div>
                  <Text type={isWithdrawal ? 'danger' : 'success'}>
                    {isWithdrawal ? '-' : '+'}¥{displayAmount}
                  </Text>
                </div>
              </List.Item>
            );
          }}
        />
      </Card>
    </div>
  );
};

export default WalletPage;