package com.demo.light.kafka;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.demo.light.bean.Order;
import com.demo.light.enums.OrderStatus;
import com.demo.light.repository.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class CloseTimeoutOrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AlipayClient alipayClient;
    @Value("${alipay.appid}")
    private String appId;

    @Scheduled(cron="0 */5 * * * ?")
    public void closeTimeoutOrders(){
        log.info("开始执行：关闭超时未支付的订单");

        try{
            LocalDateTime timeoutTime=LocalDateTime.now().minusMinutes(15);
            List<Order> timeoutOrders=orderMapper.selectTimeoutOrders(timeoutTime);

            for (Order order:timeoutOrders){
                try{
                    boolean closed=closAlipayTrade(order.getOrderId());
                    if (closed) {
                        //更新本地订单状态
                        order.setOrderStatus(OrderStatus.CLOSED);
                        order.setCloseTime(LocalDateTime.now());
                        //TODO
                        orderMapper.updateById(order);
                    }
                }catch (Exception e){
                    log.error("关闭订单失败：{}",order.getOrderId(),e);
                }
            }
        }catch (Exception e){
            log.error("关闭超时订单任务发生异常",e);
        }
        log.info("结束执行：关闭超时未支付订单");
    }

//    调用支付宝接口关闭交易
    private boolean closAlipayTrade(String outTradeNo){
        AlipayTradeCloseRequest request=new AlipayTradeCloseRequest();
        request.setBizContent("{"+"\"out_trade_no\":\""+outTradeNo+"\""+"}");
        try{
            AlipayTradeCloseResponse response=alipayClient.execute(request);
            if (response.isSuccess()){
                log.info("支付宝交易成功关闭：{}",outTradeNo);
                return true;
            }else {
                log.warn("支付宝交易关闭：{}-{}",response.getCode(),response.getMsg());
                return false;
            }
        }catch (AlipayApiException e){
            log.error("调用支付宝close接口异常",e);
            return false;
        }
    }

}
