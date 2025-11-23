// src/components/PhotographerForm.tsx
import React from 'react';
import { Form, Input, Select, InputNumber} from 'antd';
import {  LockOutlined,UserOutlined } from '@ant-design/icons';

const { Option } = Select;

const PhotographerForm: React.FC = () => {
  return (
    <>
      <h3>摄影师注册信息</h3>
          <Form.Item
        name="username"
        label="用户名"
        rules={[
          { required: true, message: '请输入用户名' },
          { min: 3, message: '用户名至少3个字符' }
        ]}
      >
        <Input prefix={<UserOutlined />} placeholder="用于登录" />
      </Form.Item>

      <Form.Item
        name="password"
        label="密码"
        rules={[
          { required: true, message: '请输入密码' },
          { min: 6, message: '密码至少6位' }
        ]}
        hasFeedback
      >
        <Input.Password prefix={<LockOutlined />} placeholder="至少6位字符" />
      </Form.Item>

      <Form.Item
        name="confirmPassword"
        label="确认密码"
        dependencies={['password']}
        hasFeedback
        rules={[
          { required: true, message: '请再次输入密码' },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('password') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error('两次密码不一致'));
            },
          }),
        ]}
      >
        <Input.Password prefix={<LockOutlined />} placeholder="请再次输入密码" />
      </Form.Item>

    
      <Form.Item
        name="realName"
        label="真实姓名"
        rules={[{ required: true, message: '请输入真实姓名' }]}
      >
        <Input prefix={<UserOutlined />} placeholder="用于实名认证" />
      </Form.Item>

      <Form.Item
        name="specialty"
        label="擅长领域"
        rules={[{ required: true, message: '请选择擅长领域' }]}
      >
        <Select mode="multiple" placeholder="可多选">
          <Option value="portrait">人像</Option>
          <Option value="wedding">婚礼</Option>
          <Option value="landscape">风光</Option>
          <Option value="commercial">商业</Option>
          <Option value="event">活动</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="experience"
        label="从业年限"
        rules={[{ required: true, message: '请输入年限' }]}
      >
        <InputNumber min={0} max={50} addonAfter="年" style={{ width: '100%' }} />
      </Form.Item>
    </>
  );
};

export default PhotographerForm;