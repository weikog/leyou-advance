package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpecController {

    @Autowired
    private SpecService specService;

    /*根据分类id查询规格组列表*/
    @GetMapping("/spec/groups/of/category")
    public ResponseEntity<List<SpecGroup>> findSpecGroupsByCid(@RequestParam("id") Long id){
        List<SpecGroup> groups = specService.findSpecGroupsByCid(id);
        return ResponseEntity.ok(groups);
    }

    /*查询规格参数列表*/
    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> findSpecParams(@RequestParam(value = "gid", required = false) Long gid,
                                                          @RequestParam(value = "cid", required = false) Long cid,
                                                          @RequestParam(value = "searching", required = false) Boolean searching){
        List<SpecParam> specParams = specService.findSpecParams(gid, cid, searching);
        return ResponseEntity.ok(specParams);
    }

    /*根据第三级分类id查询规格组和组内参数*/
    @GetMapping("/spec/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecByCid(@RequestParam("id") Long id){
        List<SpecGroupDTO> specGroupDTOS = specService.findSpecByCid(id);
        return ResponseEntity.ok(specGroupDTOS);
    }

}
