import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { BrowserRouter,Route,Routes} from 'react-router-dom'
import FirstPage from './FirstPage.tsx'
import MainPage from './photographer/MainPage.tsx'
import LoginPage from './login/LoginPage.tsx'
import Profile from './photographer/Profile.tsx'
import AuthCallbackPage from './callBack/AuthCallbackPage.tsx'
import CustomerPage from './customer/CustomerPage.tsx'
import OrderPage from './customer/OrderPage.tsx'
import WalletPage from './photographer/WalletPage.tsx'
import BindAlipayForm from './alipay/BindAlipayForm.tsx'
import CreateOrder from './customer/CreateOrder.tsx'
import OrderDetailPage from './customer/OrderDetailPage.tsx'
import PayPage from './alipay/PayPage.tsx'
import PaySuccessPage from './alipay/PaySuccessPage.tsx'
import RegisterPage from './register/RegisterPage.tsx'



createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
    <Routes>
        <Route path='/' element={<App/>}>
        <Route path='login' element={<LoginPage/>}></Route>
        <Route path='register' element={<RegisterPage/>}/>
        <Route path='photographer/profile' element={<Profile/>}></Route>
        <Route path='photographer/wallet' element={<WalletPage/>}/>
        <Route path='firstpage' element={<FirstPage/>}/>
        <Route path='alipay/bind' element={<BindAlipayForm/>}/>
        <Route path='create-order' element={<CreateOrder/>}/>
        <Route path='order/:orderId' element={<OrderDetailPage/>}/>
        <Route path='pay/success' element={<PaySuccessPage/>}/>
        <Route path='pay/:orderId' element={<PayPage/>}/>
        <Route path='customer/dashboard' element={<CustomerPage/>}/>
        <Route path='customer/order' element={<OrderPage/>}/>
        <Route path='photographer/dashboard' element={<MainPage/>} ></Route>
        <Route path="auth/callback" element={<AuthCallbackPage />} />
        </Route>
       
    </Routes>
    </BrowserRouter>
  </StrictMode>,
)
