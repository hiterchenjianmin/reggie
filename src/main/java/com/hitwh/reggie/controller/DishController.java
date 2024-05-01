package com.hitwh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hitwh.reggie.common.R;
import com.hitwh.reggie.dto.DishDto;
import com.hitwh.reggie.entity.Category;
import com.hitwh.reggie.entity.Dish;
import com.hitwh.reggie.entity.DishFlavor;
import com.hitwh.reggie.service.CategoryService;
import com.hitwh.reggie.service.DishFlavorService;
import com.hitwh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage =new Page<>();
        //构建条件构造器.
        LambdaQueryWrapper<Dish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryname = category.getName();
                dishDto.setCategoryName(categoryname);
            }

            return dishDto;
        }).collect(Collectors.toList());
//        List<DishDto> list =null;

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam Long[] ids){
        log.info("status为{}，ids为{}",status,ids);
        List<Dish> dishes = dishService.listByIds(Arrays.asList(ids));
        dishes.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("修改状态成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info("ids为{}",ids);
        dishService.removeByIds(Arrays.asList(ids));
        return R.success("删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    /**@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        return R.success(list);
    }
*/
    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

//        List<Dish> records = list.getRecords();
        List<DishDto> dtoList = list.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryname = category.getName();
                dishDto.setCategoryName(categoryname);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
//        List<DishDto> list =null;

//        dishDtoPage.setRecords(list);

//        List<DishDto>  = null;
        return R.success(dtoList);
    }

}
