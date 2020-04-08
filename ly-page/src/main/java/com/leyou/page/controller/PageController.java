package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @Autowired
    private HttpServletResponse response;

    @GetMapping("/item/{spuId}.html")
    public String toGoodsDetailPage(Model model, @PathVariable("spuId") Long spuId)throws IOException{
        Map<String, Object> spuDataMap = pageService.loadSpuData(spuId);
        model.addAllAttributes(spuDataMap);
        //页面静态化，没有静态页面就重新生成，并重定向
        pageService.createStaticItemPage(spuId);
        response.sendRedirect("http://www.leyou.com/item/"+spuId+".html");
        return "item";
    }

}
