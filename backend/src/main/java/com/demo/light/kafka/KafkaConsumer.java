package com.demo.light.kafka;

import com.demo.light.kafka.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order-events",groupId = "order-group")
    public void consumerOrderEvent(OrderEvent event){
        System.out.println("收到事件："+event.getEventType());
        if ("ORDER_CREATED".equals(event.getEventType())) {
            // 触发库存扣减、发送通知等
            System.out.println("扣减库存: " + event.getOrder());
        }else if("ORDER_PAID".equals(event.getEventType())){
            // 触发发货流程、积分增加等
            System.out.println("订单已支付，准备发货: " + event.getOrder().getOrderId());
        }
    }
}
