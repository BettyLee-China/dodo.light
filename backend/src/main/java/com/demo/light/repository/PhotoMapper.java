package com.demo.light.repository;

import com.demo.light.bean.Photo;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
@Mapper
public interface PhotoMapper {

    List<Photo> findByPhotographerId(Long photographerId);


    int addOnePhoto(Photo photo);

    Photo findById(Long id);

//    删除一个图片
    int deleteById(Long id);

    //从数据库查询批量的图片
    List<Photo> selectByIds(List<Long> ids);

    List<Photo> selectAllByUploadTime();
    //减少库存
    void deductStock(Long id,Integer deductedStock);

}
