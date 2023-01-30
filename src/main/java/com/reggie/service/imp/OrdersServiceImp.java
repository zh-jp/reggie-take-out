package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.Orders;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImp extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
