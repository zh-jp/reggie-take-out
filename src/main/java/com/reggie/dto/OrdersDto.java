package com.reggie.dto;

import com.reggie.entity.OrderDetail;
import com.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
}
