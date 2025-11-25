package com.demo.light.service;

import com.demo.light.bean.Address;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {
    Address getAddressById(Integer id,Long userId);

    void addAddress(Address address);

    Address findDefaultAddress(Long userId);

    List<Address> findAllAddress(Long userId);


    void changeDefualt(Integer id,Long userId);

}
