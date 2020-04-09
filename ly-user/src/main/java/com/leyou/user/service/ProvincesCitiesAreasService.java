package com.leyou.user.service;

import com.leyou.user.entity.Areas;
import com.leyou.user.entity.Cities;
import com.leyou.user.entity.Province;
import com.leyou.user.mapper.AreasMapper;
import com.leyou.user.mapper.CitiesMapper;
import com.leyou.user.mapper.ProvincesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.09 15:19
 */
@Service
public class ProvincesCitiesAreasService {
    /**省*/
    @Autowired
    private ProvincesMapper provincesMapper;

    @Autowired
    /**城市*/
    private CitiesMapper citiesMapper;

    @Autowired
    /**区县*/
    private AreasMapper areasMapper;

    /**
     * 查询省名列表
     * @return
     */
    public List<Province> selectProvinceList() {
        List<Province> provinces = provincesMapper.selectAll();
        return provinces;
    }

    /**
     * 根据省id查询城市列表
     * @param provinceid
     * @return
     */
    public List<Cities> selectByProvinceidCityList(Long provinceid) {
        Cities data = new Cities();
        data.setProvinceid(provinceid);
        List<Cities> cities = citiesMapper.select(data);
        return cities;
    }

    /**
     * 根据城市id查询区县列表
     * @param cityid
     * @return
     */
    public List<Areas> selectByCitiesAreaList(Long cityid) {
        Areas data = new Areas();
        data.setCityid(cityid);
        List<Areas> areas = areasMapper.select(data);
        return areas;
    }



}
