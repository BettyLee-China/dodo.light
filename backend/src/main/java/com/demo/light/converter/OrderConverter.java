package com.demo.light.converter;

import com.demo.light.bean.Address;
import com.demo.light.bean.Order;
import com.demo.light.bean.VO.OrderDetailVO;
import com.demo.light.bean.VO.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderConverter {
    OrderConverter INSTANCE= Mappers.getMapper(OrderConverter.class);

    default Address toAddress(Order order){
        if (order == null) {
            return null;
        }
        return Address.builder()
                .receiverName(order.getReceiverName())
                .phone(order.getReceiverPhone())
                .province(order.getProvince())
                .city(order.getCity())
                .district(order.getDistrict())
                .detailAddress(order.getDetailAddress())
                .build();
    }


    @Mapping(target = "items",ignore = true)
    OrderDetailVO convertToVO(Order order);
}
