// components/AddressFormModal.tsx
import React, { useEffect } from 'react';
import { Modal, Form, Input, Checkbox } from 'antd';
import useAddressStore from '../store/addressStore';

interface AddressFormValues {
  receiverName: string;
  phone: string;
  province: string;
  city: string;
  district: string;
  detailAddress: string;
  isDefault:boolean;
}

interface AddressFormModalProps {
  open: boolean;
  onOk: () => void;
  onCancel: () => void;
  addressId?: number; // 编辑模式
}

const AddressFormModal: React.FC<AddressFormModalProps> = ({
  open,
  onOk,
  onCancel,
  addressId,
}) => {
  const [form] = Form.useForm<AddressFormValues>();
  const { addAddress, updateAddress, addresses } = useAddressStore();

  const editingAddress = addresses.find(a => a.id === addressId);

  useEffect(() => {
    if (open && editingAddress) {
      form.setFieldsValue(editingAddress);
    } else if (open) {
      form.resetFields();
    }
  }, [open, editingAddress]);

  const handleSubmit = async (values: AddressFormValues) => {
    try {
      if (addressId) {
        await updateAddress(addressId, values);
      } else {
        await addAddress(values);
      }
      onOk();
    } catch (err) {
      console.error('保存地址失败', err);
    }
  };

  return (
    <Modal
      title={addressId ? '编辑地址' : '新增地址'}
      open={open}
      onOk={() => form.submit()}
      onCancel={onCancel}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{ isDefault: false }}
      >
        <Form.Item
          name="receiverName"
          label="收货人"
          rules={[{ required: true, message: '请输入收货人' }]}
        >
          <Input placeholder="张三" />
        </Form.Item>

        <Form.Item
          name="phone"
          label="手机号"
          rules={[
            { required: true, message: '请输入手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' },
          ]}
        >
          <Input placeholder="13800138000" />
        </Form.Item>

        <Form.Item
          name="province"
          label="省"
          rules={[{ required: true, message: '请输入省' }]}
        >
          <Input placeholder="广东省" />
        </Form.Item>

        <Form.Item
          name="city"
          label="市"
          rules={[{ required: true, message: '请输入市' }]}
        >
          <Input placeholder="深圳市" />
        </Form.Item>

        <Form.Item
          name="district"
          label="区"
          rules={[{ required: true, message: '请输入区' }]}
        >
          <Input placeholder="南山区" />
        </Form.Item>

        <Form.Item
          name="detailAddress"
          label="详细地址"
          rules={[{ required: true, message: '请输入详细地址' }]}
        >
          <Input.TextArea placeholder="XX街道XX号" rows={2} />
        </Form.Item>

        <Form.Item name="isDefault" valuePropName="checked">
          <Checkbox>设为默认地址</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddressFormModal;