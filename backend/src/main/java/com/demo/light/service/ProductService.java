package com.demo.light.service;

import com.demo.light.bean.ProductSnapshot;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ProductService {
    List<ProductSnapshot> getProductSnapshot(List<Long> productIds);

    //获取单个商品的快照
    ProductSnapshot getProductSnapshot(Long productId);
}
