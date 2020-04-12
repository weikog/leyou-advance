package com.leyou.item.service;

import com.leyou.common.constant.LyConstants;
import com.leyou.common.constant.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.dto.AllMenuDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.MenuDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryGroup;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


    public List<Category> findCategoryByPid(Long pid) {
        //封装条件
        Category record = new Category();
        record.setParentId(pid);
        //根据条件做查询
        List<Category> list = categoryMapper.select(record);
        //判空
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> findCategorysByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }



    //一级菜单组
    public List<CategoryGroup> getCategoryGroups() {
        List<CategoryGroup> categoryGroups = categoryMapper.getCategoryGroups();

        if (CollectionUtils.isEmpty(categoryGroups)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryGroups;
    }


    //首页导航加载全部菜单
    public List<AllMenuDTO> getAllMenu() {
        //获取一级菜单组
        List<CategoryGroup> categoryGroups = categoryMapper.getCategoryGroups();
        //把一级菜单组属性复制到AllMenuDTO
        List<AllMenuDTO> allMenuDTOS = BeanHelper.copyWithCollection(categoryGroups, AllMenuDTO.class);
        //遍历获取二级三级菜单
        allMenuDTOS.forEach(allMenuDTO -> {
            String[] cids = allMenuDTO.getCategoryIds().split(",");
            List<MenuDTO> menuDTOS = new LinkedList<>();
            for (int i = 0; i < cids.length; i++) {
                List<Category> secondCategorys = findCategoryByPid(Long.valueOf(cids[i]));
                secondCategorys.forEach(secondCategory ->{
                    MenuDTO menuDTO = new MenuDTO();
                    menuDTO.setId(secondCategory.getId());
                    menuDTO.setName(secondCategory.getName());
                    List<Category> categorys = findCategoryByPid(secondCategory.getId());
                    List<CategoryDTO> thirCategorys = BeanHelper.copyWithCollection(categorys, CategoryDTO.class);
                    menuDTO.setThirCategorys(thirCategorys);
                    menuDTOS.add(menuDTO);
                });
            }
            allMenuDTO.setMenuDTOS(menuDTOS);
        });
        return allMenuDTOS;
    }
    
    /*public void addRedis(){
        String indexMenuKey = LyConstants.INDEX_MENU_KEY;
        BoundHashOperations<String, String, String> indexMenuMap = redisTemplate.boundHashOps(indexMenuKey);

        List<AllMenuDTO> allMenuDTOS = getAllMenu();
        allMenuDTOS.forEach(allMenuDTO -> {
            indexMenuMap.put(allMenuDTO.getGroupId().toString() , JsonUtils.toString(allMenuDTO));
        });

    }*/

    /**
     * 添加首页菜单数据到redis
     */
    public void addRedis(){
        String indexMenuKey = LyConstants.INDEX_MENU_KEY;
        List<AllMenuDTO> allMenuDTOS = getAllMenu();
        redisTemplate.opsForValue().set(indexMenuKey,JsonUtils.toString(allMenuDTOS));
    }


    /*public List<AllMenuDTO> findMenu(){
        String indexMenuKey = LyConstants.INDEX_MENU_KEY;
        BoundHashOperations<String, String, String> indexMenuMap = redisTemplate.boundHashOps(indexMenuKey);
        List<String> menus = indexMenuMap.values();
        if (CollectionUtils.isEmpty(menus)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<AllMenuDTO> allMenuDTOS = menus.stream()
                .map(menu -> JsonUtils.toBean(menu, AllMenuDTO.class))
                .collect(Collectors.toList());
        return allMenuDTOS;
    }*/

    /**
     * 在redis中查找首页菜单数据
     * @return
     */
    public List<AllMenuDTO> findMenu(){
        String indexMenuKey = LyConstants.INDEX_MENU_KEY;
        if (!redisTemplate.hasKey(indexMenuKey)){
            addRedis();
        }

        String menus = redisTemplate.opsForValue().get(indexMenuKey);

        List<AllMenuDTO> allMenuDTOS = JsonUtils.toList(menus, AllMenuDTO.class);
        return allMenuDTOS;
    }

    /**
     * 后台管理系统修改分类，并发送到mq异步更新首页导航菜单的redis数据
     * @param id
     * @param name
     */
    public void editCategory(Long id, String name) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        category.setName(name);
        categoryMapper.updateByPrimaryKeySelective(category);

        //分类名称修改的同时，也更新首页导航菜单数据
        String routingKey = MQConstants.RoutingKey.CATEGORY_UPDATE_KEY;
        amqpTemplate.convertAndSend(MQConstants.Exchange.CATEGORY_EXCHANGE_NAME,routingKey,id);
    }

    public void delCategory(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
        //分类名称修改的同时，也更新首页导航菜单数据
        String routingKey = MQConstants.RoutingKey.CATEGORY_UPDATE_KEY;
        amqpTemplate.convertAndSend(MQConstants.Exchange.CATEGORY_EXCHANGE_NAME,routingKey,id);
    }
}
