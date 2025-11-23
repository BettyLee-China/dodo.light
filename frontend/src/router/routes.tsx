

import BindAlipayForm from '../alipay/BindAlipayForm';
import PayPage from '../alipay/PayPage';
import PaySuccessPage from '../alipay/PaySuccessPage';
import App from '../App';
import CreateOrder from '../customer/CreateOrder';
import CustomerPage from '../customer/CustomerPage';
import OrderDetailPage from '../customer/OrderDetailPage';
import FisrtPage from '../FirstPage';
import MainPage from '../photographer/MainPage';
import Profile from '../photographer/Profile';
import WalletPage from '../photographer/WalletPage';
import RegisterPage from '../register/RegisterPage';

const routes=[
    {
        path:'/',element:<App/>
    },
     {
        path:'/firstpage',element:<FisrtPage/>
    },

{
    path:'photographer/dashboard',element:<MainPage/>
},
{
    path:'photographer/profile',element:<Profile/>
},
{
    path:'photographer/wallet' ,element:<WalletPage/>
},
{
    path:'customer/dashboard',element:<CustomerPage/>
},
{
    path:'alipay/bind',element:<BindAlipayForm/>
},
{
    path:'create-order',element:<CreateOrder/>
},
{
    path:'order/:orderId',element:<OrderDetailPage/>
}
,
{
    path:'pay/success',element:<PaySuccessPage/>
},
{
    path:'pay/:orderId',element:<PayPage/>
},
{
    path:'register',element:<RegisterPage/>
},

]

export default routes;