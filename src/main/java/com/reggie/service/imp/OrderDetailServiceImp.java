package com.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.OrderDetail;
import com.reggie.mapper.OrderDetailMapper;
import com.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImp extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
