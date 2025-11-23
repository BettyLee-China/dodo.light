import React, { useState, useRef, useEffect } from 'react';
import Cropper from 'react-easy-crop';
import Modal from 'react-modal';
import dayjs from 'dayjs';
import {
  Card,
  DatePicker,
  Flex,
  Input,
  message,
  Select,
  Typography,
  Space,
  Button,
  Avatar,
} from 'antd';
import { ManOutlined, WomanOutlined, EditOutlined } from '@ant-design/icons';
import { useUserProfileStore } from '../store/userProfileStore';
import { getUserIdFromToken } from '../utils/auth';

Modal.setAppElement('#root');

const cardStyle: React.CSSProperties = {
  width: 650,
  maxWidth: '90%',
  margin: '0 auto',
};

interface Point {
  x: number;
  y: number;
}

interface Area {
  x: number;
  y: number;
  width: number;
  height: number;
}

type GenderEnum = '男' | '女';

const CustomerPage: React.FC = () => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [crop, setCrop] = useState<Point>({ x: 0, y: 0 });
  const [zoom, setZoom] = useState<number>(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState<Area | null>(null);
  const [croppedImageUrl, setCroppedImageUrl] = useState<string | null>(null);
  const [open, setOpen] = useState<boolean>(false);

  const [isEditingNickname, setIsEditingNickname] = useState<boolean>(false);
  const [isEditingBirthday, setIsEditingBirthday] = useState<boolean>(false);
  const [isEditingGender, setIsEditingGender] = useState<boolean>(false);
  const [isEditingBio, setIsEditingBio] = useState<boolean>(false);

  const [bio, setBio] = useState<string>('');
  const [nickname, setNickname] = useState<string>('');
  const [birthday, setBirthday] = useState<string | null>(null);
  const [gender, setGender] = useState<GenderEnum>('男');

  const fileInputRef = useRef<HTMLInputElement>(null);
  const cropperRef = useRef<React.ElementRef<typeof Cropper>>(null);

  const {
    getProfile,
    nickname: storeNickname,
    gender: storeGender,
    birthday: storeBirthday,
    avatar,
    bio: storeBio,
    modifyProfile,
  } = useUserProfileStore();

  const userId = getUserIdFromToken();

  // ✅ 安全初始化状态：确保类型严格匹配
  useEffect(() => {
    if (userId) {
      getProfile(userId);
    }

    // nickname: 必须是 string
    setNickname(typeof storeNickname === 'string' ? storeNickname : '');

    // gender: 只接受 '男' 或 '女'
    if (storeGender === '女') {
      setGender('女');
    } else {
      setGender('男'); // 包括 null / undefined / '男'
    }

    // birthday: 格式化为 YYYY-MM-DD 或 null
    if (storeBirthday) {
      setBirthday(dayjs(storeBirthday).format('YYYY-MM-DD'));
    } else {
      setBirthday(null);
    }

    // bio: 必须是 string
    setBio(typeof storeBio === 'string' ? storeBio : '');
  }, [getProfile, userId, storeBio, storeBirthday, storeGender, storeNickname]);

  const getCroppedImg = (): Promise<{ file: File; url: string } | null> => {
    return new Promise((resolve) => {
      if (!croppedAreaPixels || !cropperRef.current?.imageRef?.current) {
        return resolve(null);
      }

      const image = cropperRef.current.imageRef.current;
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');

      if (!ctx) return resolve(null);

      canvas.width = croppedAreaPixels.width;
      canvas.height = croppedAreaPixels.height;

      ctx.drawImage(
        image,
        croppedAreaPixels.x,
        croppedAreaPixels.y,
        croppedAreaPixels.width,
        croppedAreaPixels.height,
        0,
        0,
        croppedAreaPixels.width,
        croppedAreaPixels.height
      );

      canvas.toBlob(
        (blob) => {
          if (!blob) return resolve(null);
          const file = new File([blob], `avatar-${Date.now()}.jpg`, { type: 'image/jpeg' });
          const url = URL.createObjectURL(blob);
          resolve({ file, url });
        },
        'image/jpeg',
        0.9
      );
    });
  };

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      message.warning('请上传图片文件');
      return;
    }
    const url = URL.createObjectURL(file);
    setSelectedImage(url);
    setOpen(true);
  };

  const onCropComplete = (_: Area, croppedAreaPixels: Area) => {
    setCroppedAreaPixels(croppedAreaPixels);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedImage(null);
    setZoom(1);
    setCrop({ x: 0, y: 0 });
    setCroppedAreaPixels(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    if (croppedImageUrl) {
      URL.revokeObjectURL(croppedImageUrl);
      setCroppedImageUrl(null);
    }
  };

  const handleCrop = async () => {
    const result = await getCroppedImg();
    if (result) {
      if (croppedImageUrl) URL.revokeObjectURL(croppedImageUrl);
      setCroppedImageUrl(result.url);
      const currentUserId = getUserIdFromToken();
      if (currentUserId) {
        useUserProfileStore.getState().uploadAvatar(currentUserId, result.file);
        message.success('头像更新成功');
      }
      handleClose();
    }
  };

  const updateField = async (field: string, value: string) => {
    const currentUserId = getUserIdFromToken();
    if (!currentUserId) {
      message.error('用户未登录');
      return;
    }
    const formData = new FormData();
    formData.append(field, value);
    try {
      await modifyProfile(currentUserId, formData);
      message.success(`${getFieldLabel(field)}更新成功`);
    } catch (err) {
      message.error(`${getFieldLabel(field)}更新失败`);
      console.error(err);
    }
  };

  const getFieldLabel = (field: string) => {
    const labels: Record<string, string> = {
      nickname: '昵称',
      gender: '性别',
      birthday: '生日',
      bio: '简介',
    };
    return labels[field] || '信息';
  };

  const handleNicknameChange = () => {
    if (nickname !== (storeNickname ?? '')) {
      updateField('nickname', nickname);
    }
    setIsEditingNickname(false);
  };

  const handleGenderChange = (value: GenderEnum) => {
    setGender(value);
    if (value !== (storeGender ?? '男')) {
      updateField('gender', value);
    }
    setIsEditingGender(false);
  };

  const handleBirthdayChange = (
    _: dayjs.Dayjs | null,
    dateString: string | string[]
  ) => {
    const finalDate = Array.isArray(dateString) ? dateString[0] : dateString;
    setBirthday(finalDate || null);
    if (finalDate && finalDate !== storeBirthday) {
      updateField('birthday', finalDate);
    }
    setIsEditingBirthday(false);
  };

  const handleBioChange = () => {
    if (bio !== (storeBio ?? '')) {
      updateField('bio', bio);
    }
    setIsEditingBio(false);
  };

  // ✅ Avatar 的 src 支持 string | undefined，无需强制 as string
  const displayAvatar = croppedImageUrl || avatar || undefined;

  return (
    <div style={{ padding: '32px 20px', background: '#f5f7fa', minHeight: '100vh' }}>
      <Card hoverable style={cardStyle} styles={{ body: { padding: 24 } }}>
        <Flex gap="large" align="flex-start">
          {/* 头像区域 */}
          <div style={{ textAlign: 'center', flexShrink: 0 }}>
            <div
              onClick={() => fileInputRef.current?.click()}
              style={{
                cursor: 'pointer',
                position: 'relative',
                display: 'inline-block',
              }}
            >
              <Avatar
                size={120}
                src={displayAvatar}
                icon={<ManOutlined />}
                style={{ backgroundColor: '#f5f5f5', border: '2px solid #fff', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}
              />
              <div
                style={{
                  position: 'absolute',
                  bottom: 0,
                  right: 0,
                  backgroundColor: '#1890ff',
                  borderRadius: '50%',
                  padding: '4px',
                  color: '#fff',
                  fontSize: '12px',
                }}
              >
                <EditOutlined />
              </div>
            </div>
            <input
              type="file"
              accept="image/*"
              onChange={onFileChange}
              ref={fileInputRef}
              style={{ display: 'none' }}
            />

            <Typography.Title level={5} style={{ marginTop: 16, marginBottom: 8 }}>
              {isEditingNickname ? (
                <Input
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  onPressEnter={handleNicknameChange}
                  onBlur={handleNicknameChange}
                  autoFocus
                  style={{ width: 160 }}
                />
              ) : (
                <Space>
                  {storeNickname || '未设置昵称'}
                  <EditOutlined
                    style={{ fontSize: 14, color: '#1890ff', cursor: 'pointer' }}
                    onClick={() => {
                      setNickname(storeNickname ?? '');
                      setIsEditingNickname(true);
                    }}
                  />
                </Space>
              )}
            </Typography.Title>
          </div>

          {/* 信息编辑区 */}
          <div style={{ flex: 1, minWidth: 0 }}>
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              {/* 性别 */}
              <Flex justify="space-between" align="center">
                <Typography.Text strong>性别</Typography.Text>
                {isEditingGender ? (
                  <Select
                    value={gender}
                    onChange={handleGenderChange}
                    options={[
                      { label: '男', value: '男' },
                      { label: '女', value: '女' },
                    ]}
                    autoFocus
                    style={{ width: 120 }}
                  />
                ) : (
                  <Space>
                    {storeGender === '女' ? (
                      <WomanOutlined style={{ color: '#eb2f96' }} />
                    ) : (
                      <ManOutlined style={{ color: '#1890ff' }} />
                    )}
                    <Typography.Text>{storeGender || '未设置'}</Typography.Text>
                    <EditOutlined
                      style={{ fontSize: 14, color: '#1890ff', cursor: 'pointer' }}
                      onClick={() => {
                        setGender(storeGender === '女' ? '女' : '男');
                        setIsEditingGender(true);
                      }}
                    />
                  </Space>
                )}
              </Flex>

              {/* 生日 */}
              <Flex justify="space-between" align="center">
                <Typography.Text strong>生日</Typography.Text>
                {isEditingBirthday ? (
                  <DatePicker
                    value={birthday ? dayjs(birthday) : null}
                    onChange={handleBirthdayChange}
                    format="YYYY-MM-DD"
                    autoFocus
                    style={{ width: 160 }}
                  />
                ) : (
                  <Space>
                    <Typography.Text>
                      {storeBirthday ? dayjs(storeBirthday).format('YYYY-MM-DD') : '未设置'}
                    </Typography.Text>
                    <EditOutlined
                      style={{ fontSize: 14, color: '#1890ff', cursor: 'pointer' }}
                      onClick={() => setIsEditingBirthday(true)}
                    />
                  </Space>
                )}
              </Flex>

              {/* 简介 */}
              <div>
                <Flex justify="space-between" align="center" style={{ marginBottom: 8 }}>
                  <Typography.Text strong>个人简介</Typography.Text>
                  {!isEditingBio && (
                    <EditOutlined
                      style={{ fontSize: 14, color: '#1890ff', cursor: 'pointer' }}
                      onClick={() => {
                        setBio(storeBio ?? '');
                        setIsEditingBio(true);
                      }}
                    />
                  )}
                </Flex>
                {isEditingBio ? (
                  <Input.TextArea
                    value={bio}
                    onChange={(e) => setBio(e.target.value)}
                    onBlur={handleBioChange}
                    onPressEnter={(e) => {
                      if (!e.shiftKey) {
                        e.preventDefault();
                        handleBioChange();
                      }
                    }}
                    rows={3}
                    maxLength={200}
                    showCount
                    autoFocus
                  />
                ) : (
                  <Typography.Paragraph
                    style={{ margin: 0, color: storeBio ? undefined : '#999' }}
                    ellipsis={{ rows: 3, expandable: true }}
                  >
                    {storeBio || '暂无简介'}
                  </Typography.Paragraph>
                )}
              </div>
            </Space>
          </div>
        </Flex>
      </Card>

      {/* 裁剪模态框 */}
      <Modal
        isOpen={open}
        onRequestClose={handleClose}
        contentLabel="裁剪头像"
        style={{
          content: {
            width: '90%',
            height: '85%',
            maxHeight: 600,
            margin: 'auto',
            padding: 0,
            borderRadius: '12px',
            inset: 'unset',
            display: 'flex',
            flexDirection: 'column',
          },
          overlay: {
            backgroundColor: 'rgba(0,0,0,0.6)',
            zIndex: 1000,
          },
        }}
      >
        <div
          style={{
            background: '#000',
            padding: '12px',
            textAlign: 'center',
            color: '#fff',
          }}
        >
          <Typography.Title level={5} style={{ margin: 0, color: '#fff' }}>
            裁剪头像
          </Typography.Title>
        </div>

        <div style={{ flex: 1, position: 'relative', minHeight: 0 }}>
          <Cropper
            image={selectedImage || ''}
            crop={crop}
            zoom={zoom}
            aspect={1}
            cropShape="round"
            onCropChange={setCrop}
            onCropComplete={onCropComplete}
            onZoomChange={setZoom}
            ref={cropperRef}
          />
        </div>

        <div
          style={{
            padding: '16px',
            background: '#fff',
            borderTop: '1px solid #eee',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <Typography.Text>缩放：</Typography.Text>
            <input
              type="range"
              value={zoom}
              min="1"
              max="3"
              step="0.1"
              onChange={(e) => setZoom(parseFloat(e.target.value))}
              style={{ width: 150 }}
            />
          </div>
          <Space>
            <Button onClick={handleClose}>取消</Button>
            <Button type="primary" onClick={handleCrop} disabled={!selectedImage}>
              确定
            </Button>
          </Space>
        </div>
      </Modal>
    </div>
  );
};

export default CustomerPage;