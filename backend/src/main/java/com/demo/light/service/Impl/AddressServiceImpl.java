package com.demo.light.service.Impl;

import com.demo.light.bean.Address;
import com.demo.light.repository.AddressMapper;
import com.demo.light.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Address getAddressById(Integer id, Long userId) {
        Address address=addressMapper.selectAddressById(id,userId);
        if (address == null) {
            throw new RuntimeException("收货地址不存在或无权限访问");
        }

        return address;
    }

    @Override
    public void addAddress(Address address) {
        addressMapper.insertAddress(address);
    }

    @Override
    public Address findDefaultAddress(Long userId) {
        return addressMapper.selectDefaultAddress(userId);
    }

    @Override
    public List<Address> findAllAddress(Long userId) {
        return addressMapper.selectAllByUserId(userId);
    }
    //将某个地址改为新的默认地址
    //这个步骤有点多
    @Override
    public void changeDefualt(Integer id,Long userId) {
        //先查找该用户本来的默认地址
        Address preAddress = addressMapper.selectDefaultAddress(userId);
        addressMapper.cancelDefault(preAddress.getId());
        addressMapper.setDefault(id);
    }
}
