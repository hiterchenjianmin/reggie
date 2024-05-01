package com.hitwh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hitwh.reggie.entity.OrderDetail;
import com.hitwh.reggie.mapper.OrderDetailMapper;
import com.hitwh.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
