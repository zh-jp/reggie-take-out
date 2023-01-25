package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要同时操作两张数据表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);

    // 删除菜品和口味
    public void deleteWithFlavor(Long id);

}
