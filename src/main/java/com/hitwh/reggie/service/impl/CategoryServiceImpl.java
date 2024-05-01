package com.hitwh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hitwh.reggie.common.CustomException;
import com.hitwh.reggie.entity.Category;
import com.hitwh.reggie.entity.Dish;
import com.hitwh.reggie.entity.Setmeal;
import com.hitwh.reggie.mapper.CategoryMapper;
import com.hitwh.reggie.service.CategoryService;
import com.hitwh.reggie.service.DishService;
import com.hitwh.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    /**
     * 删除数据,判断是否关联了菜品，如果已经关联，就抛出业务异常
     * @param id
     */

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishlambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishlambdaQueryWrapper);

        if(count1>0){
            //关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            //关联了菜品，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(id);
    }
}
