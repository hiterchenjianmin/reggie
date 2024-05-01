package com.hitwh.reggie.controller;

import com.hitwh.reggie.common.R;
import com.hitwh.reggie.entity.Orders;
import com.hitwh.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session){
        log.info("orders:{}",orders);
        orderService.submit(orders,session);
        return R.success("下单成功");
    }
}
