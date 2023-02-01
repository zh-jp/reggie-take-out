package com.reggie.controller;


import com.reggie.common.R;
import com.reggie.dto.OrdersDto;
import com.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/userPage")
    public R<OrdersDto> userPage(Integer page, Integer pageSize) {
        return null;
    }
}
