package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索页面商品分页查询
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> goodsPageQuery(@RequestBody SearchRequest request){
        PageResult<GoodsDTO> pageResult = searchService.goodsPageQuery(request);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 过滤条件数据查询
     */
    @PostMapping("/filter")
    public ResponseEntity<Map<String, List<?>>> queryFilterParams(@RequestBody SearchRequest request){
        Map<String, List<?>> filterParamsMap = searchService.queryFilterParams(request);
        return ResponseEntity.ok(filterParamsMap);
    }




}
