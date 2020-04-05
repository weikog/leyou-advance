package com.leyou.user.controller;

import com.leyou.user.dto.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("address")
public class AddressController {

    /**
     * 根据
     * @param userId 用户id
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping
    public ResponseEntity<AddressDTO> queryAddressById(@RequestParam("userId") Long userId, @RequestParam("id") Long id){
        AddressDTO address = new AddressDTO();
        address.setId(1L);
        address.setStreet("珠吉路58号津安创业园一层黑马程序员");
        address.setCity("广州");
        address.setDistrict("天河区");
        address.setAddressee("小飞飞");
        address.setPhone("15800000000");
        address.setProvince("广东");
        address.setPostcode("510000");
        address.setIsDefault(true);
        return ResponseEntity.ok(address);
    }
}