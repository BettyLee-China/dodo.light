// src/pages/RegisterPage.tsx
import React, { useState } from 'react';
import { Steps, Card, Row, Col, Button, message, Form, Space } from 'antd';
import { UserOutlined, CameraOutlined } from '@ant-design/icons';
import CustomerForm from './components/CustomerForm';
import PhotographerForm from './components/PhotographerForm';
import request from '../utils/request';



type UserType = 'customer' | 'photographer' | null;

const RegisterPage: React.FC = () => {
  const [current, setCurrent] = useState(0);
  const [userType, setUserType] = useState<UserType>(null);
  const [form] = Form.useForm();

  const next = () => {
    if (current === 0) {
      if (!userType) {
        message.warning('请选择您的身份');
        return;
      }
      setCurrent(1);
    } else if (current === 1) {
      form
        .validateFields()
        .then((values) => {
          // 合并角色和表单数据
          const finalData = { userType, ...values };
          console.log('提交数据:', finalData);
          message.success('注册成功！');

        const response= request.post("/register",finalData);
        console.log(response);
          // TODO: 调用 API 提交
        })
        .catch(() => {
          message.error('请检查表单填写是否正确');
        });
    }
  };

  const prev = () => {
    setCurrent(0);
    form.resetFields(); // 返回时清空第二步表单
  };

  const steps = [
    { title: '选择身份' },
    { title: '填写信息' },
  ];

  return (
    <div style={{ minHeight: '100vh', background: '#f0f2f5', padding: '40px 20px' }}>
      <Card style={{ maxWidth: 700, margin: '0 auto' }}>
        <Steps current={current} items={steps} />

        <div style={{ marginTop: 32 }}>
          {current === 0 && (
            <div>
              <h2 style={{ textAlign: 'center', marginBottom: 32 }}>请选择您的身份</h2>
              <Row gutter={[24, 24]}>
                <Col xs={24} sm={12}>
                  <Card
                    hoverable
                    onClick={() => setUserType('customer')}
                    style={{
                      border: userType === 'customer' ? '2px solid #1890ff' : '1px solid #f0f0f0',
                      backgroundColor: userType === 'customer' ? '#e6f7ff' : '#fff',
                      borderRadius: 12,
                    }}
                  >
                    <Space direction="vertical" align="center" style={{ width: '100%' }}>
                      <div
                        style={{
                          width: 64,
                          height: 64,
                          borderRadius: '50%',
                          background: '#e6f7ff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: '#1890ff',
                          fontSize: 28,
                        }}
                      >
                        <UserOutlined />
                      </div>
                      <h3>我是客户</h3>
                      <p style={{ color: '#888', textAlign: 'center' }}>
                        浏览作品 · 预约拍摄 · 收藏喜欢的摄影师
                      </p>
                    </Space>
                  </Card>
                </Col>
                <Col xs={24} sm={12}>
                  <Card
                    hoverable
                    onClick={() => setUserType('photographer')}
                    style={{
                      border: userType === 'photographer' ? '2px solid #52c41a' : '1px solid #f0f0f0',
                      backgroundColor: userType === 'photographer' ? '#f6ffed' : '#fff',
                      borderRadius: 12,
                    }}
                  >
                    <Space direction="vertical" align="center" style={{ width: '100%' }}>
                      <div
                        style={{
                          width: 64,
                          height: 64,
                          borderRadius: '50%',
                          background: '#f6ffed',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: '#52c41a',
                          fontSize: 28,
                        }}
                      >
                        <CameraOutlined />
                      </div>
                      <h3>我是摄影师</h3>
                      <p style={{ color: '#888', textAlign: 'center' }}>
                        发布作品 · 接单拍摄 · 打造个人品牌
                      </p>
                    </Space>
                  </Card>
                </Col>
              </Row>
            </div>
          )}

          {current === 1 && (
            <Form form={form} layout="vertical" initialValues={{}}>
              {userType === 'customer' && <CustomerForm />}
              {userType === 'photographer' && <PhotographerForm />}
            </Form>
          )}
        </div>

        <div style={{ marginTop: 40, textAlign: 'right' }}>
          {current > 0 && (
            <Button style={{ margin: '0 8px' }} onClick={prev}>
              上一步
            </Button>
          )}
          <Button type="primary" onClick={next}>
            {current === steps.length - 1 ? '提交注册' : '下一步'}
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default RegisterPage;