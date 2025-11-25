package com.demo.light.service.Impl;

import com.demo.light.bean.Photo;
import com.demo.light.bean.ProductSnapshot;
import com.demo.light.repository.PhotoMapper;
import com.demo.light.service.MinioService;
import com.demo.light.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private PhotoMapper photoMapper;
    @Autowired
    private MinioService minioService;

    @Override
    public List<ProductSnapshot> getProductSnapshot(List<Long> productIds) {

        //先把objectName取出来放在集合里
        List<Photo> photoList = photoMapper.selectByIds(productIds);

        Map<String,String> urlMap=new HashMap<>();
        for (Photo photo:photoList){
            String imageUrl = minioService.getPresignedUrl(photo.getObjectName(), Duration.ofDays(7));
            urlMap.put(photo.getObjectName(), imageUrl);
        }
         return  photoList.stream().map(
                photo -> ProductSnapshot.builder()
                        .productId(photo.getId())
                        .productName(photo.getTitle())
                        .objectName(photo.getObjectName())
                        .price(photo.getPrice())
                        .stock(photo.getStock())
                        .imageUrl(urlMap.get(photo.getObjectName()))
                        .build()).collect(Collectors.toList());

    }

    @Override
    public ProductSnapshot getProductSnapshot(Long productId) {
        Photo photo = photoMapper.findById(productId);
        String objectName = photo.getObjectName();
        String url = minioService.getPresignedUrl(objectName, Duration.ofDays(7));
        ProductSnapshot ps=ProductSnapshot.builder()
                .productId(productId)
                .productName(photo.getTitle())
                .objectName(photo.getObjectName())
                .price(photo.getPrice())
                .stock(photo.getStock())
                .imageUrl(url)
                .build();
        return ps;
    }
}
