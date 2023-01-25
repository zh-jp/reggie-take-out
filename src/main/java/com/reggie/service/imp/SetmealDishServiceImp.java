package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetmealDishMapper;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImp extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
