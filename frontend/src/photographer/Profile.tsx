// src/photographer/Profile.tsx
import React, { useState } from 'react';
import {
  Form,
  Input,
  InputNumber,
  Upload,
  Button,
  message,
  Card,
  Typography,
  Row,
  Col,
  Divider,
  Select
} from 'antd';
import {
  UploadOutlined,
  EditOutlined,
  DollarOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { PhotoData } from '../types/PhotoData';
import { postPhoto } from '../api/photographer';
import { getUserIdFromToken } from '../utils/auth';
import type { UploadChangeParam } from 'antd/es/upload';
import type { UploadFile } from 'antd/es/upload/interface';
import type {ValidateErrorEntity} from 'rc-field-form/es/interface';

const { Title, Text } = Typography;



const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  // 处理选择的照片分类
  const handlePhotoMode = (value: string) => {
    console.log(`selected ${value}`);
  };

  // 处理文件选择
  const handleFileChange = (info: UploadChangeParam<UploadFile>) => {
    const { file } = info;
    const originFile = file.originFileObj;
    if (!originFile) return;

    const isImage = originFile.type.startsWith('image/');
    const isLt5M = originFile.size / 1024 / 1024 < 5;

    if (!isImage) {
      message.error('只能上传图片文件！');
      return;
    }
    if (!isLt5M) {
      message.error('图片必须小于5MB！');
      return;
    }

    setSelectedFile(originFile);

    const reader = new FileReader();
    reader.onload = () => {
      setPreviewImage(reader.result as string);
    };
    reader.readAsDataURL(originFile);
  };

  // 处理预览
  const handlePreview = (file: UploadFile) => {
    if (file.url || file.preview) {
      const preview=file.url || file.preview;
      setPreviewImage(preview??null);
    } else if (file.originFileObj) {
      setPreviewImage(URL.createObjectURL(file.originFileObj));
    }
  };

  // 移除预览
  const handleRemovePreview = () => {
    setPreviewImage(null);
    form.setFieldsValue({ file: null });
  };

  // 表单提交
  const onFinish = async (values: PhotoData) => {
    setUploading(true);
    try {
      if (!selectedFile) {
        message.error('请先选择图片！');
        return;
      }

      const formData = new FormData();
      const rawId = getUserIdFromToken();

      // ✅ 关键修复：防止 undefined 传给 FormData
      if (typeof rawId !== 'string' || !rawId.trim()) {
        message.error('用户身份无效，请重新登录');
        return;
      }

      formData.append('image', selectedFile);
      formData.append('title', values.title);
      formData.append('description', values.description);
      formData.append('price', values.price.toString());
      formData.append('photoMode', values.photoMode);
      formData.append('photographerId', rawId);

      const response = await postPhoto(formData);
      console.log(response.data);

      // 重置表单
      form.resetFields();
      setPreviewImage(null);
      setSelectedFile(null);

      // 跳转到作品管理页面
      navigate('/photographer/dashboard');
    } catch (error) {
      console.error('Upload error:', error);
      message.error('保存失败，请重试');
    } finally {
      setUploading(false);
    }
  };

  // 表单校验失败
  const onFinishFailed = (errorInfo:ValidateErrorEntity<PhotoData>) => {
    console.log('Failed:', errorInfo);
    message.error('请检查表单填写是否正确');
  };

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: '20px' }}>
      <Card>
        <Title level={2} style={{ textAlign: 'center', marginBottom: 32 }}>
          <EditOutlined /> 上传摄影作品
        </Title>

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          initialValues={{
            price: 0
          }}
        >
          {/* 照片预览区域 */}
          <Form.Item label="照片预览" name="file" required>
            <div
              style={{
                border: '1px dashed #d9d9d9',
                borderRadius: 8,
                padding: 20,
                textAlign: 'center',
                backgroundColor: '#fafafa'
              }}
            >
              {previewImage ? (
                <div style={{ position: 'relative' }}>
                  <img
                    src={previewImage}
                    alt="预览"
                    style={{
                      maxWidth: '100%',
                      maxHeight: 300,
                      objectFit: 'contain',
                      borderRadius: 4
                    }}
                  />
                  <Button
                    type="primary"
                    danger
                    size="small"
                    style={{ position: 'absolute', top: 8, right: 8 }}
                    onClick={handleRemovePreview}
                  >
                    移除
                  </Button>
                </div>
              ) : (
                <Upload
                  name="photo"
                  onChange={handleFileChange}
                  onPreview={handlePreview}
                  accept="image/*"
                  maxCount={1}
                  showUploadList={false}
                >
                  <Button icon={<UploadOutlined />}>
                    选择照片文件
                  </Button>
                  <Text type="secondary" style={{ display: 'block', marginTop: 8 }}>
                    支持 JPG、PNG、GIF 格式，最大 5MB
                  </Text>
                </Upload>
              )}
            </div>
          </Form.Item>

          <Divider />

          {/* 表单内容 */}
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item
                label="照片标题"
                name="title"
                rules={[
                  { required: true, message: '请输入照片标题' },
                  { min: 2, message: '标题至少2个字符' },
                  { max: 50, message: '标题最多50个字符' }
                ]}
              >
                <Input
                  placeholder="为您的作品起个吸引人的标题"
                  prefix={<EditOutlined />}
                  showCount
                  maxLength={50}
                />
              </Form.Item>
            </Col>

            <Col span={24}>
              <Form.Item
                label="照片描述"
                name="description"
                rules={[
                  { required: true, message: '请输入照片描述' },
                  { min: 10, message: '描述至少10个字符' }
                ]}
              >
                <Input.TextArea
                  rows={4}
                  placeholder="描述您的创作灵感、拍摄地点、技术参数等"
                  showCount
                  maxLength={500}
                />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="销售价格 (元)"
                name="price"
                rules={[
                  { required: true, message: '请输入销售价格' },
                  {
                    type: 'number',
                    min: 0,
                    message: '价格不能为负数'
                  }
                ]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  max={9999}
                  step={0.01}
                  precision={2}
                  placeholder="0.00"
                  prefix={<DollarOutlined />}
                />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="分类标签"
                name="photoMode"
                rules={[{ required: true, message: '请选择分类标签' }]}
              >
                <Select
                  onChange={handlePhotoMode}
                  options={[
                    { value: 'LANDSCAPE', label: '风景' },
                    { value: 'PORTRAIT', label: '人像' },
                    { value: 'NIGHT', label: '夜景' },
                    { value: 'MACRO', label: '微距' },
                    { value: 'SPORTS', label: '运动' },
                    { value: 'FOOD', label: '美食' }
                  ]}
                />
              </Form.Item>
            </Col>
          </Row>

          <Divider />

          {/* 提交按钮 */}
          <Form.Item style={{ textAlign: 'center', marginTop: 24 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={uploading}
              size="large"
              style={{ minWidth: 120 }}
            >
              {uploading ? '上传中...' : '确认上传'}
            </Button>

            <Button onClick={() => navigate('/my-photos')} style={{ marginLeft: 16 }}>
              取消
            </Button>
          </Form.Item>
        </Form>

        {/* 提示信息 */}
        <div style={{ marginTop: 24, padding: 16, backgroundColor: '#f6f6f6', borderRadius: 8 }}>
          <Text strong>上传须知：</Text>
          <ul style={{ margin: '8px 0 0 20px', lineHeight: 1.6 }}>
            <li>请确保您拥有照片的版权</li>
            <li>高质量的照片更容易获得关注</li>
            <li>合理定价有助于作品销售</li>
            <li>审核通过后作品将上线展示</li>
          </ul>
        </div>
      </Card>
    </div>
  );
};

export default Profile;