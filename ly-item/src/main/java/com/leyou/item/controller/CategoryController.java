package com.leyou.item.controller;

import com.leyou.item.dto.AllMenuDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类的父id查询分类列表
     * 如果当前处理器要被其他服务通过feign调用。
     * 那么简单类型的参数必须加@RequestParam
     */
    @GetMapping("/category/of/parent")
    public ResponseEntity<List<Category>> findCategoryByPid(@RequestParam("pid") Long pid){
        List<Category> list = categoryService.findCategoryByPid(pid);
        //return ResponseEntity.status(HttpStatus.OK).body(list);
        return ResponseEntity.ok(list);//效果等于上面一行的写法
    }

    /*根据分类id的集合查询分类对象的集合*/
    @GetMapping("/category/list")
    public ResponseEntity<List<Category>> findCategorysByIds(@RequestParam("ids") List<Long> ids){
        List<Category> list = categoryService.findCategorysByIds(ids);
        return ResponseEntity.ok(list);
    }


    /**
     * 首页导航功能，查找全部三级菜单
     * @param
     * @return
     */
    @GetMapping("/category/menu")
    public ResponseEntity<List<AllMenuDTO>> findMenu(){
        List<AllMenuDTO> allMenuDTOS = categoryService.findMenu();
        return ResponseEntity.ok(allMenuDTOS);
    }

    /**
     * 后台系统分类管理修改分类名称
     * @param id
     * @param name
     * @return
     */
    @GetMapping("/category/edit")
    public ResponseEntity<Void> editCategory(@RequestParam("id") Long id,@RequestParam("name") String name){
        categoryService.editCategory(id,name);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 后台系统分类管理删除分类名称
     * @param id
     * @return
     */
    @GetMapping("/category/del")
    public ResponseEntity<Void> delCategory(@RequestParam("id") Long id){
        categoryService.delCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
