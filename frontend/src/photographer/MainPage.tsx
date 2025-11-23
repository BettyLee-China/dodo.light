import  { useEffect } from 'react';
import { Card, Col, Row, Spin } from 'antd';
import usePhotoStore from '../store/usePhotoStore';
import { getUserIdFromToken } from '../utils/auth';
import MainCard from './MainCard';
import type { Photo } from '../types/Photo'; // 引入类型

export default function MainPage() {
  const { photos, fetchPhotos, loading } = usePhotoStore();
  const photographerId = getUserIdFromToken();

  useEffect(() => {
    if (photographerId) {
      fetchPhotos(photographerId);
    }
  }, [fetchPhotos, photographerId]);

  // 如果没有登录（photographerId 为 null），可选处理
  if (!photographerId) {
    return (
      <div style={{ padding: '30px', textAlign: 'center' }}>
        请先登录
      </div>
    );
  }

  return (
    <div style={{ background: '#ECECEC', padding: '30px' }}>
      <MainCard />

      {/* 加载状态 */}
      {loading ? (
        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <Spin size="large" />
        </div>
      ) : (
        <Row gutter={[16, 16]} style={{ marginTop: '24px' }}>
          {photos.length === 0 ? (
            <Col span={24}>
              <div style={{ textAlign: 'center', color: '#999' }}>暂无作品</div>
            </Col>
          ) : (
            photos.map((photo: Photo) => (
              <Col span={8} key={photo.id}>
                <Card
                  bordered={false}
                  title={photo.title}
                  bodyStyle={{ paddingBottom: 12 }}
                >
                  <img
                    src={photo.url} // 确保这是完整可访问的 URL
                    alt={photo.title}
                    style={{
                      width: '100%',
                      height: 180,
                      objectFit: 'cover',
                      borderRadius: 4,
                    }}
                  />
                  <div style={{ marginTop: 8 }}>
                    <div><strong>描述：</strong>{photo.description}</div>
                    <div><strong>价格：</strong>¥{photo.price.toFixed(2)}</div>
                  </div>
                </Card>
              </Col>
            ))
          )}
        </Row>
      )}
    </div>
  );
}