package com.leyou.user.controller;

import com.leyou.user.entity.Area;
import com.leyou.user.entity.City;
import com.leyou.user.entity.Province;
import com.leyou.user.service.AddressService;
import com.leyou.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserInfoService userInfoService;

    /*
    * 查询所有的省份
    * */
    @GetMapping("/province")
    public ResponseEntity<List<Province>> queryProvince(){
        List<Province> provinces =addressService.queryProvince();
        return ResponseEntity.ok(provinces);
    }
    /*
     * 查询所有的市
     * */
    @PostMapping("/city")
    public ResponseEntity<List<City>> queryCity(@RequestBody String province){
        List<City> cities =addressService.queryCity(province);
        return ResponseEntity.ok(cities);
    }

    /*
     * 查询所有的市区和县
     * */
    @PostMapping("/area")
    public ResponseEntity<List<Area>> queryArea(@RequestBody String city){
        List<Area> areas =addressService.queryArea(city);
        return ResponseEntity.ok(areas);
    }
}