package com.demo.light.kafka;

import com.demo.light.bean.Order;
import com.demo.light.kafka.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private String orderTopic;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public KafkaProducer(@Value("${kafka.topic.order}")String orderTopic, KafkaTemplate<String,OrderEvent> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
        this.orderTopic=orderTopic;
    }

    public void sendOrderEvent(String eventType, Order order){
        OrderEvent event= OrderEvent.builder()
                .eventType(eventType)
                .order(order)
                .build();
        kafkaTemplate.send(orderTopic,order.getOrderId(),event);
    }
}
