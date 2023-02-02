package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.OrdersDto;
import com.reggie.entity.AddressBook;
import com.reggie.entity.OrderDetail;
import com.reggie.entity.Orders;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.AddressBookService;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrdersService;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    @Transactional
    public R<String> submit(@RequestBody Orders orders) {
        Long usrId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, usrId);
        // 查询该用户购物车内的产品，并删除数据表的数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        // 统计购物车产品金额
        int totalAmount = 0;
        for (ShoppingCart i : shoppingCartList) {
            Integer number = i.getNumber();
            Integer amount = i.getAmount().intValue();
            totalAmount += number * amount;
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        // 完善orders信息
        orders.setStatus(1);
        orders.setUserId(usrId);
        orders.setOrderTime(localDateTime);
        orders.setAmount(new BigDecimal(totalAmount));
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        // 暂时没有username
        orders.setConsignee(addressBook.getConsignee());
        // 先存进去再取出来，因为需要order_id，还需要在order_detail表中保存
        ordersService.save(orders);

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据用户Id和订单创建时间确定唯一订单
        ordersLambdaQueryWrapper.eq(Orders::getUserId, usrId);
        ordersLambdaQueryWrapper.eq(Orders::getOrderTime, localDateTime);
        orders = ordersService.getOne(ordersLambdaQueryWrapper);
        Long ordersId = orders.getId();
        for (ShoppingCart i : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(i, orderDetail);
            orderDetail.setOrderId(ordersId);
            orderDetailService.save(orderDetail);
        }
        return R.success("提交成功！");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize) {
        Long usrId = BaseContext.getCurrentId();
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, usrId);
        queryWrapper.orderByAsc(Orders::getStatus);
        ordersService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");
        List<Orders> list = pageInfo.getRecords();
        List<OrdersDto> records = list.stream().map((item) -> {
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(records);
        return R.success(ordersDtoPage);
    }
}
