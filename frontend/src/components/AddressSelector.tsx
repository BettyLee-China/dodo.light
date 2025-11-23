// components/AddressSelector.tsx
import React, { useState, useEffect } from 'react';
import { Button, List, Modal, Typography, Space, Tag } from 'antd';
import { PlusOutlined, EnvironmentOutlined } from '@ant-design/icons';
import useAddressStore from '../store/addressStore';
import AddressFormModal from './AddressFormModal';

const { Text } = Typography;

interface AddressSelectorProps {
  value?: number; // 受控模式：外部传入选中的 ID
  onChange?: (id: number) => void; // 选中回调
}

const AddressSelector: React.FC<AddressSelectorProps> = ({ value, onChange }) => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const { addresses, selectedAddressId, fetchAddresses, selectAddress } = useAddressStore();

  const selectedId = value !== undefined ? value : selectedAddressId;

  useEffect(() => {
    fetchAddresses();
  }, []);

  const handleSelect = (id: number) => {
    if (onChange) {
      onChange(id);
    } else {
      selectAddress(id);
    }
    setIsModalVisible(false);
  };

  const handleAdd = () => {
    setIsModalVisible(true);
  };

  const handleModalClose = () => {
    setIsModalVisible(false);
    fetchAddresses(); // 刷新地址列表
  };

  const selectedAddress = addresses.find(a => a.id === selectedId);

  return (
    <>
      {/* 当前选中地址展示 */}
      <div
        onClick={() => setIsModalVisible(true)}
        style={{
          border: '1px solid #d9d9d9',
          borderRadius: 4,
          padding: 12,
          cursor: 'pointer',
          minHeight: 60,
        }}
      >
        {selectedAddress ? (
          <Space direction="vertical" size={2}>
            <Space>
              <EnvironmentOutlined style={{ color: '#ff4d4f' }} />
              <Text strong>{selectedAddress.receiverName} • {selectedAddress.phone}</Text>
              {selectedAddress.isDefault && <Tag color="blue">默认</Tag>}
            </Space>
            <Text type="secondary">
              {selectedAddress.province}{selectedAddress.city}{selectedAddress.district}
              {selectedAddress.detailAddress}
            </Text>
          </Space>
        ) : (
          <Text type="secondary">请选择收货地址</Text>
        )}
      </div>

      {/* 地址选择弹窗 */}
      <Modal
        title="选择收货地址"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
        width={500}
      >
        <List
          dataSource={addresses}
          renderItem={address => (
            <List.Item
              actions={[
                <Button
                  type="link"
                  key="select"
                  onClick={() => handleSelect(address.id)}
                >
                  选择
                </Button>,
              ]}
              onClick={() => handleSelect(address.id)}
              style={{ cursor: 'pointer' }}
            >
              <List.Item.Meta
                avatar={<EnvironmentOutlined style={{ fontSize: 20, color: '#ff4d4f' }} />}
                title={
                  <Space>
                    {address.receiverName} • {address.phone}
                    {address.isDefault && <Tag color="blue">默认</Tag>}
                  </Space>
                }
                description={`${address.province}${address.city}${address.district}${address.detailAddress}`}
              />
            </List.Item>
          )}
        />

        <Button
          type="dashed"
          block
          icon={<PlusOutlined />}
          onClick={handleAdd}
          style={{ marginTop: 16 }}
        >
          新增收货地址
        </Button>
      </Modal>

      {/* 添加/编辑地址表单 */}
      <AddressFormModal
        open={isModalVisible && !addresses.length} // 第一次进入时直接显示表单
        onOk={handleModalClose}
        onCancel={() => setIsModalVisible(false)}
      />
    </>
  );
};

export default AddressSelector;