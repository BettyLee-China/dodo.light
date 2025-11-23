// components/Navbar.tsx
import React from 'react';
import { Layout, Menu, Modal } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import menuConfig from '../config/menu';
import { useUserProfileStore } from '../store/userProfileStore';
import useAddressStore from '../store/addressStore';
import { useOrderStore } from '../store/orderStore';
import request from '../utils/request';

const { Header } = Layout;

const NavBar: React.FC = () => {
  const navigate = useNavigate();
  const { isLogin, user, logout } = useAuthStore();
  const { clearProfile } = useUserProfileStore();
  const { clearAddress } = useAddressStore();
  const { clearOrders } = useOrderStore();
  const [modalVisible, setModalVisible] = React.useState(false);

  const handleLogout = () => {
    setModalVisible(true);
  };

  const confirmLogout = async () => {
    try {
      const response = await request.post("/logout");
      if (String(response.data.msg) === "成功") {
        logout();
        clearProfile();
        clearAddress();
        clearOrders();
        setModalVisible(false);
        navigate('/');
      } else {
        alert("退出失败");
      }
    } catch (error) {
      console.error("退出请求失败:", error);
      alert("网络错误，请重试");
    }
  };

  const cancelLogout = () => {
    setModalVisible(false);
  };

  const handleMenuClick = (e: { key: string }) => {
    if (e.key === 'logout') {
      handleLogout();
    } else {
      navigate(`/${e.key}`);
    }
  };

  const getMenuItem = () => {
    const items = [...menuConfig.common];

    if (isLogin && user) {
      if (user.role === 'customer') {
        items.push(...menuConfig.customer);
      }
      if (user.role === 'photographer') {
        items.push(...menuConfig.photographer);
      }
      items.push({
        key: 'logout',
        label: `退出(${user.username})`, // ✅ 纯字符串
      });
    } else {
      items.push(
        { key: 'login', label: '登录' },
        { key: 'register', label: '注册' }
      );
    }

    return items;
  };

  const menuItems = getMenuItem();
  const currentPath = window.location.pathname.slice(1) || 'firstpage';

  return (
    <>
      <Header style={{ position: 'sticky', top: 0, zIndex: 1, width: '100%' }}>
        <div
          className="logo"
          style={{
            float: 'left',
            width: 120,
            height: 31,
            margin: '16px 24px 16px 0',
            background: 'rgba(255, 255, 255, 0.2)',
            borderRadius: 6,
          }}
        />
        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[currentPath]} // ✅ 包成数组
          style={{ lineHeight: '64px' }}
          onClick={handleMenuClick}
          items={menuItems}
        />
      </Header>

      <Modal
        title="确认退出"
        open={modalVisible}
        onOk={confirmLogout}
        onCancel={cancelLogout}
        okText="退出"
        cancelText="取消"
      >
        <p>确定要退出登录吗？</p>
      </Modal>
    </>
  );
};

export default NavBar;