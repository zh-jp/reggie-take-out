package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishFlavorMapper;
import com.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImp extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
