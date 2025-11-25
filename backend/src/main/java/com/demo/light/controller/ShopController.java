package com.demo.light.controller;

import com.demo.light.bean.Photo;
import com.demo.light.bean.VO.PhotoVO;
import com.demo.light.converter.PhotoConverter;
import com.demo.light.result.R;
import com.demo.light.service.MinioService;
import com.demo.light.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private PhotoService photoService;

    private static final Logger log= LoggerFactory.getLogger(ShopController.class);
    @GetMapping("/products")
    public R<List<PhotoVO>> getProducts(){
        List<PhotoVO> photos = photoService.findAllByUploadTime();
        return R.OK(photos);
    }
    @GetMapping("/detail/{productId}")
    public R<PhotoVO> getProductDetail(@PathVariable Long productId){
        PhotoVO photo = photoService.generateVOById(productId);
        return R.OK(photo);
    }

}
