package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/item/{spuId}.html")
    public String toGoodsDetailPage(Model model, @PathVariable("spuId") Long spuId){
        Map<String, Object> spuDataMap = pageService.loadSpuData(spuId);
        model.addAllAttributes(spuDataMap);
        return "item";
    }

}
