import React from 'react';
import { Layout} from 'antd';
import {Outlet} from 'react-router-dom'

import NavBar from './components/NavBar'
const { Content, Footer } = Layout;



const App: React.FC = () => {

  

  return (
    <Layout>
      {/* <Header
        className='sticky top-0 z-10 w-full flex items-center bg-white shadow-sm'
      >
        <p>光影社区</p>
        <div className="demo-logo" />
       
          <Button type="link" onClick={handleFirstPage}>首页</Button>
         <Button type="link" onClick={handleLogin}>登录</Button>
      </Header> */}
      <NavBar/>
      <Content className='p-0 bg-slate-200'>
        {/* <Breadcrumb
          className='my-4'
          items={[{ title: 'Home' }, { title: 'List' }, { title: 'App' }]}
        /> */}
        <div
          className="p-6 min-h-[380px] bg-gray-100 rounded-lg"
        >
          <Outlet/>
        </div>
      </Content>
      <Footer 
      className='text-center bg-white'>
        Ant Design ©{new Date().getFullYear()} Created by Ant UED
      </Footer>
    </Layout>
  );
};

export default App;