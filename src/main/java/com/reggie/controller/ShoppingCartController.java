package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.OrdersDto;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long usrId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, usrId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long usrId = BaseContext.getCurrentId();
        shoppingCart.setUserId(usrId);
        // 查询当前菜品或套餐是否已经加入购物车，若已加入则数量上加1即可。
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, usrId);
        if (shoppingCart.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        // 若已存在，则在基础上加1
        if (shoppingCartOne != null) {
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long usrId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, usrId);
        if (shoppingCart.getDishId() != null) {
            Long dishId = shoppingCart.getDishId();
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            shoppingCart = shoppingCartService.getOne(queryWrapper);

        } else {
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
            shoppingCart = shoppingCartService.getOne(queryWrapper);
        }
        Integer number = shoppingCart.getNumber();
        if (number == 1) {
            shoppingCartService.remove(queryWrapper);
        } else {
            shoppingCart.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        Long usrId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, usrId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功！");
    }
}
