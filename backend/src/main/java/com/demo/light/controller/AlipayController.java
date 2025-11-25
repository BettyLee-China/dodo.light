package com.demo.light.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.demo.light.annotation.CurrentUser;
import com.demo.light.bean.DTO.RefundRequestDTO;
import com.demo.light.bean.DTO.WithdrawRequest;
import com.demo.light.bean.Order;
import com.demo.light.bean.User;
import com.demo.light.enums.CodeEnum;
import com.demo.light.enums.OrderStatus;
import com.demo.light.kafka.OrderService;
import com.demo.light.result.R;
import com.demo.light.service.Impl.WalletServiceImpl;
import com.demo.light.service.RefundService;
import com.demo.light.service.WithdrawalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private RefundService refundService;
    @Autowired
    private WalletServiceImpl walletService;
    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final Logger log= LoggerFactory.getLogger(AlipayController.class);

    @Value("${alipay.notify-url}")
    private String notifyUrl;
    @Value("${alipay.return-url}")
    private String returnUrl;
    @Value("${alipay.public-key}")
    private String alipayPublicKey;
    @Value("${alipay.redirect-uri}")
    private String redirectUri;
    @Value("${alipay.appid}")
    private String appId;
    @Value("${alipay.gateway}")
    private String geteway;


    @Autowired
    private OrderService orderService;

    @GetMapping("/pay")
    public R<String> createAlipay(@RequestParam String orderId) throws AlipayApiException{
        System.out.println("进入alipaycontroller");
        //先要查看订单是否存在
        Order order = orderService.getOrderByOrderNo(orderId);
        System.out.println("订单："+order);

        if (!"PENDING_PAYMENT".equals(order.getOrderStatus().name())){
            return R.FAIL(CodeEnum.BAD_REQUEST);
        }

        //创建API入参对象
        AlipayTradeWapPayRequest request=new AlipayTradeWapPayRequest();
        //设置异步通知地址（服务器接口）
        request.setNotifyUrl(notifyUrl);
        //设置同步返回地址（支付完成后前端跳转）
        request.setReturnUrl(returnUrl);

        //构建业务参数
        JSONObject bizContent=new JSONObject();
        bizContent.put("out_trade_no",orderId);
        bizContent.put("total_amount",order.getPayAmount());
        bizContent.put("subject","订单支付-"+orderId);
        bizContent.put("product_code","QUICK_WAP_WAY");

        request.setBizContent(bizContent.toString());


        AlipayTradeWapPayResponse response=alipayClient.pageExecute(request);

        if (response.isSuccess()){

            return R.OK(response.getBody());
        }else {
            throw  new RuntimeException("支付宝下单失败"+response.getBody());
        }
    }

    @PostMapping("/notify")//这个必须要返回String
    public String handleNotify(HttpServletRequest request)
    throws AlipayApiException, IOException {
        System.out.println("notify。。。。。。");
        StringBuilder sb=new StringBuilder();
        BufferedReader reader=request.getReader();

        String line;

        while((line= reader.readLine())!=null){
            sb.append(line);
        }
        String body=sb.toString();
        log.info("收到支付宝异步通知，参数:{}",body);


        Map<String,String> params=new HashMap<>();
        for (String param : body.split("&")){
            String[] pair=param.split("=",2);
            if (pair.length == 2) {
                String key= URLDecoder.decode(pair[0],"UTF-8");
                String value=URLDecoder.decode(pair[1],"UTF-8");
                params.put(key,value);
            }
        }
        //获取所有参数


        //验证签名，防止签名伪造
        boolean signVerified= AlipaySignature.rsaCheckV1(
                params,
                alipayPublicKey,
                "UTF-8",
                "RSA2"
        );
        System.out.println("获得到的签名："+signVerified);
        log.info("获取到的签名：{}",signVerified);
        if (signVerified) {
            String tradeStatus=params.get("trade_status");
            String outTradeNo=params.get("out_trade_no");
            log.info("订单当前的状态：{}",tradeStatus);

            if("TRADE_SUCCESS".equals(tradeStatus)||"TRADE_FINISHED".equals(tradeStatus)){
                log.info("✅ 收到支付宝支付成功通知，订单号: {}, 状态: {}", outTradeNo, tradeStatus);
                //TODO 调用服务更改支付状态
                orderService.setOrderStatus(outTradeNo, OrderStatus.PAID);
                log.info("✅ 订单状态已更新为 PAID");
            }
            return "success";
        }else {
            log.error("处理支付宝通知异常");
            return "failure";
        }

    }

    @PostMapping("/refund/execute")
    public R<String> alipayRefund(@NotNull @Valid @RequestBody RefundRequestDTO dto){

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setOutTradeNo(dto.getOutTradeNo());
        model.setOutRequestNo(dto.getOutRequestNo());
        model.setRefundAmount(dto.getRefundAmount().toString());
        model.setRefundReason(dto.getReason());

        R<String> createResult=refundService.createRefund(model);
        if (createResult.getCode()==200) {
            //进行退款
            return refundService.processRefund(dto.getOutRequestNo());
        }else {
            return R.FAIL(CodeEnum.BAD_REQUEST);
        }

    }

    @PostMapping("/bind-alipay")
    public R<String> bindAlipay(@RequestParam Long userId,
                           @RequestParam String authCode){
        walletService.bindAlipayByAuthCode(userId, authCode);
        return R.OK("绑定成功");
    }

    @PostMapping("/withdraw")
    public R<String> withdraw(@org.springframework.web.bind.annotation.RequestBody WithdrawRequest request,
                              @CurrentUser User currentUser){
        withdrawalService.createWithdrawal(currentUser.getUser().getId(), request);
        return R.OK("提现申请已提交，请耐心等待回复");
    }

    @PostMapping("/auth-url")
    public R<String> getAuthUrl(){
        try{
            String encodedRedirectUri=URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());

            String state=UUID.randomUUID().toString().replace("-","");

            redisTemplate.opsForValue().set("alipay_auth_state:"+state,"valid",5,TimeUnit.MINUTES);

            // 3. 构建授权 URL
            String authUrl = geteway;

            return R.OK(authUrl);
        }catch (Exception e){
            throw  new RuntimeException("生成支付宝授权链接失败",e);
        }
    }


    @GetMapping("/callback")
    public void handleAuthCallback(
            @RequestParam("auth_code") String authCode,
            @RequestParam("state") String state,
            HttpServletResponse response) throws Exception {

        // 1. 验证 state（防 CSRF）
        String stateKey = "alipay_auth_state:" + state;
        String valid = redisTemplate.opsForValue().get(stateKey);
        if (valid == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state");
            return;
        }
        redisTemplate.delete(stateKey); // 一次性使用

        // 2. 调用绑定接口（复用你已有的逻辑）
        // 注意：这里你需要知道 userId！但回调时支付宝只给 auth_code
        // 所以通常做法是：让用户先登录，再点击“绑定支付宝”，此时 userId 已知

        // ⚠️ 问题：你当前的 bindAlipay 需要 userId，但回调 URL 没带！
        // 解决方案 A：把 userId 放进 state（不推荐，暴露用户ID）
        // 解决方案 B：让用户先登录，绑定时将 userId 存入 session 或临时 token

        // 临时方案：假设你通过某种方式能拿到当前用户（比如从 session）
        // 这里演示：将 auth_code 存入 Redis，前端轮询获取后调用 /bind-alipay
        String tempKey = "alipay_auth_code:" + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(tempKey, authCode, 5, TimeUnit.MINUTES);

        // 3. 重定向到前端页面，并带上临时 key
        String frontendUrl = "https://localhost:4000/bind-success?token=" + tempKey;
        response.sendRedirect(frontendUrl);
    }

    @GetMapping("/login")
    public void redirectToAlipayAuth(HttpServletResponse response) throws Exception {
        String state = UUID.randomUUID().toString().replace("-", "");

        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());
        String authUrl = "https://openapi.alipaydev.com/gateway.do/oauth2/publicAppAuthorize.htm?" +
                "app_id=" + appId +
                "&scope=user_id" +
                "&redirect_uri=" + encodedRedirectUri +
                "&state=" + state;

        // 存 state 到 Redis（可选，用于安全校验）
        redisTemplate.opsForValue().set("alipay_state:" + state, "1", 10, TimeUnit.MINUTES);

        response.sendRedirect(authUrl);
    }
}
