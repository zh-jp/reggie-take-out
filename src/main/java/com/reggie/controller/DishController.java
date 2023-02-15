package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

//    @Autowired
//    private RedisTemplate redisTemplate;

    @CacheEvict(value = "dishCache", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @Cacheable(value = "dishCache", key = "#p0 + '_' + #p1 + '_' + #p2")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");   // records 就是展现在页面上的集合
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); //分类Id
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoPage);
    }

    /**
     * 根据id来查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @Cacheable(value = "dishCache", key = "#p0")
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 编辑菜品信息
     *
     * @param dishDto
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        // 以下是RedisTemplate 的处理方式，现在改用注解的方式
        // 更改数据库后清空缓存（对缓存内全部数据）
//        Set keys = redisTemplate.keys("Category*");
//        redisTemplate.delete(keys);
        // 精确清理具体的分类
//        String key = "Category" + dishDto.getCategoryId();
//        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    /**
     * 逻辑删除菜品和口味
     *
     * @param ids
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            dishService.deleteWithFlavor(id);
        }
        return R.success("删除成功");
    }

    @CacheEvict(value = "dishCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, status);
        dishService.update(updateWrapper);
        return R.success("状态变更成功");
    }

    @Cacheable(value = "dishCache", key = "#p0.categoryId")
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.getList(dish);
        return R.success(dishDtoList);
    }
}
