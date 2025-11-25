package com.demo.light.repository;

import com.demo.light.bean.Address;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressMapper {
    //TODO其实我不懂这里为什么要传两个参数
    Address selectAddressById(Integer id,Long userId);

    //存储一个address
    void insertAddress(Address address);

    //找默认地址
    Address selectDefaultAddress(Long userId);

    //找某个用户的所有地址
    List<Address> selectAllByUserId(Long userId);

    //设定默认的地址
    void setDefault(Integer id);

    //取消默认地址
    void cancelDefault(Integer id);

}
