package com.demo.light.converter;

import com.demo.light.bean.Photo;
import com.demo.light.bean.DTO.PhotoDto;
import com.demo.light.bean.VO.PhotoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PhotoConverter {

    @Mapping(target = "uploadTime", ignore = true)
    @Mapping(target = "objectName", ignore = true)
    @Mapping(target = "stock", ignore = true)
    Photo dtoToEntity(PhotoDto photoDto);


    PhotoVO convertToVO(Photo photo);
}
