package com.hitwh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hitwh.reggie.common.CustomException;
import com.hitwh.reggie.dto.SetmealDto;
import com.hitwh.reggie.entity.Setmeal;
import com.hitwh.reggie.entity.SetmealDish;
import com.hitwh.reggie.mapper.SetMealMapper;
import com.hitwh.reggie.service.SetMealService;
import com.hitwh.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //操作菜品和套餐关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表数据
        this.removeByIds(ids);
        //删除关系表数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper1);


    }
}
