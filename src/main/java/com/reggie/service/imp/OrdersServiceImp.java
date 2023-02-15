package com.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.entity.AddressBook;
import com.reggie.entity.OrderDetail;
import com.reggie.entity.Orders;
import com.reggie.entity.ShoppingCart;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.AddressBookService;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrdersService;
import com.reggie.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImp extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    AddressBookService addressBookService;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        Long usrId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, usrId);
        // 查询该用户购物车内的产品，并删除数据表的数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空！");
        }
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        // 统计购物车产品金额
        AtomicInteger totalAmount = new AtomicInteger(0);   // 原子操作，适用于多线程情况
        for (ShoppingCart i : shoppingCartList) {
            totalAmount.addAndGet(i.getAmount().multiply(new BigDecimal(i.getNumber())).intValue());
        }
        Long orderId = IdWorker.getId();
        // 完善orders信息
        orders.setId(orderId);
        orders.setStatus(1);
        orders.setNumber(orderId.toString());
        orders.setUserId(usrId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(totalAmount.intValue()));
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                        (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                        (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setUserName(addressBook.getConsignee());
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);
        for (ShoppingCart i : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(i, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailService.save(orderDetail);
        }
    }
}
