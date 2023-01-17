package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.Dish;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {
}
