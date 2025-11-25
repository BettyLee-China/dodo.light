package com.demo.light.converter;


import com.demo.light.bean.Address;
import com.demo.light.bean.DTO.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressConverter {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Address convertToEntity(AddressDTO addressDTO);
}
