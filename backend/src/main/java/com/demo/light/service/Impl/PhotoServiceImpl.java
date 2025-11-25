package com.demo.light.service.Impl;

import com.demo.light.bean.Photo;
import com.demo.light.bean.VO.PhotoVO;
import com.demo.light.converter.PhotoConverter;
import com.demo.light.repository.PhotoMapper;
import com.demo.light.service.MinioService;
import com.demo.light.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private PhotoMapper photoMapper;
    @Autowired
    private  PhotoConverter photoConverter;
    @Autowired
    private MinioService minioService;
    public static final Logger log= LoggerFactory.getLogger(PhotoServiceImpl.class);
    @Override
    public List<Photo> selectByPhotographerId(Long id) {
        return photoMapper.findByPhotographerId(id);
    }

    @Override
    public int saveOnePhoto(Photo photo) {
        return photoMapper.addOnePhoto(photo);
    }

    public Long generateId(){

        return ThreadLocalRandom.current().nextLong();
    }

    @Override
    public Photo selectById(Long id) {
        Photo photo = photoMapper.findById(id);
        return photo;
    }

    @Override
    public PhotoVO generateVOById(Long id) {
        Photo photo=photoMapper.findById(id);
        PhotoVO vo = photoConverter.convertToVO(photo);
        String url = minioService.getPresignedUrl(photo.getObjectName(), Duration.ofDays(7));
        vo.setUrl(url);
        return vo;
    }

    @Override
    public int decreaseById(Long id) {
        return photoMapper.deleteById(id);
    }

    @Override
    public List<Photo> findByIds(List<Long> ids) {
        return photoMapper.selectByIds(ids);
    }
    //查询所有的图片（商品）,并且需要返回正确的url
    @Override
    public List<PhotoVO> findAllByUploadTime() {
        //从数据中查询出数据
        List<Photo> photoList = photoMapper.selectAllByUploadTime();
        for (Photo p : photoList) {
            log.info("从 DB 查出的 Photo: id={}, objectName={}", p.getId(), p.getObjectName());
        }
        //提取所有的objectName
        Set<String> objectNames=photoList.stream()
                .map(Photo::getObjectName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String,String> urlMap=new HashMap<>();


        for (String objectName:objectNames){
            try{
                String url=minioService.getPresignedUrl(objectName, Duration.ofDays(7));
                urlMap.put(objectName,url);
            }catch (Exception e){
                throw new RuntimeException("获取url失败");
            }
        }

        return photoList.stream().map(
                photo -> {
                    PhotoVO photoVO=photoConverter.convertToVO(photo);
                    photoVO.setUrl(urlMap.getOrDefault(photo.getObjectName(),"/default-image.png"));
                    log.info("转换后的 VO id: {}", photoVO.getId());
                    return photoVO;
                }
        ).collect(Collectors.toList());

    }

    @Override
    public void decreaseStock(Long id, Integer deductedStock) {
        photoMapper.deductStock(id, deductedStock);
    }
}
