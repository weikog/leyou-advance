package com.leyou.user.service;

import com.leyou.user.entity.Area;
import com.leyou.user.entity.City;
import com.leyou.user.entity.Province;
import com.leyou.user.mapper.AreaMapper;
import com.leyou.user.mapper.CityMapper;
import com.leyou.user.mapper.ProvinceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class AddressService {
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private AreaMapper areaMapper;


    //查询所有省份
    public List<Province> queryProvince() {
        List<Province> provinceList = provinceMapper.selectAll();
        return provinceList;
    }

    //查询省下所有市
    public List<City> queryCity(String province) {
        //查询省份id
        Province record = new Province();
        record.setProvince(province);
        Province pro = provinceMapper.selectOne(record);
        Integer provinceId = Integer.parseInt(pro.getProvinceid());

        //查询父id为省代号的所有市
        City city = new City();
        city.setProvinceid(provinceId.toString());
        List<City> cityList = cityMapper.select(city);
        return cityList;
    }

    //查询市下所有区
    public List<Area> queryArea(String city) {
        //获取市id
        City record = new City();
        record.setCity(city);
        City city1 = cityMapper.selectOne(record);
        String cityId = city1.getCityid();

        Example example = new Example(Area.class);
        example.createCriteria().andEqualTo("cityid",cityId);
        List<Area> areaList = areaMapper.selectByExample(example);
        return areaList;
    }
}
