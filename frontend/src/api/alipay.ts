import request from "../utils/request";

// 获取支付宝授权跳转链接
export const getAlipayAuthUrl=async()=>{
   const response= await request.post('/alipay/auth-url');
   return response.data.data;

}

// 用 auth_code 绑定支付宝
export interface BindAlipayParams {
  authCode: string;
  state: string;
}

export const bindAlipayWithAuthCode = async (params: BindAlipayParams): Promise<void> => {
  await request.post('/alipay/bind-alipay', params);
};