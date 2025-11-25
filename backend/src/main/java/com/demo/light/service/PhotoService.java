package com.demo.light.service;

import com.demo.light.bean.Photo;
import com.demo.light.bean.VO.PhotoVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhotoService {
    List<Photo> selectByPhotographerId(Long id);
    int saveOnePhoto(Photo photo);

    Photo selectById(Long id);

    PhotoVO generateVOById(Long id);

    int decreaseById(Long id);

    List<Photo> findByIds(List<Long> ids);

    List<PhotoVO> findAllByUploadTime();

    //减少库存
    void decreaseStock(Long id,Integer deductedStock);

}
