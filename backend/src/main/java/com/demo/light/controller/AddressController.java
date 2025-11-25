package com.demo.light.controller;

import com.demo.light.bean.Address;
import com.demo.light.bean.DTO.AddressDTO;
import com.demo.light.converter.AddressConverter;
import com.demo.light.result.R;
import com.demo.light.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private AddressConverter addressConverter;

    @PostMapping("/add")
    public R<Address> addAddress(@RequestBody AddressDTO addressDTO){
        System.out.println("进入controller");
        Address address=addressConverter.convertToEntity(addressDTO);
        address.setId(UUID.randomUUID().hashCode()&Integer.MAX_VALUE);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        addressService.addAddress(address);
        return R.OK(address);
    }

    @GetMapping("/default")
    public R<Address> getDefaultAddress(@RequestParam("userId") Long userId){
       return R.OK(addressService.findDefaultAddress(userId));
    }
    //获得所有的id
    @GetMapping("/{userId}")
    public R<List<Address>> getAddressById(@PathVariable("userId") Long userId){
        return R.OK(addressService.findAllAddress(userId));
    }

}
