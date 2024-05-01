package com.hitwh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hitwh.reggie.common.R;
import com.hitwh.reggie.entity.ShoppingCart;
import com.hitwh.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        log.info("shoppingCart:{}",shoppingCart);
        //设置用户id,指定哪个用户的购物车
        Long currentId = (Long)session.getAttribute("user");
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或套餐是否在购物车里面
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart!=null){
            //如果存在，就在原有的数值上面加一
            Integer number = cart.getNumber();
            cart.setNumber(number+1);
            shoppingCartService.updateById(cart);
        }else {
            //如果不存在，添加到购物车，默认数量为一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart=shoppingCart;
        }
        return R.success(cart);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        log.info("shoppingCart:{}",shoppingCart);
        Long currentId = (Long)session.getAttribute("user");
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        ShoppingCart cart =null;
        if(dishId!=null){
            //减少的是菜品
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart dish = shoppingCartService.getOne(queryWrapper);
            if(dish.getNumber()>1){
                dish.setNumber(dish.getNumber()-1);
                cart = dish;
                shoppingCartService.updateById(dish);
            }else {
                shoppingCartService.remove(queryWrapper);
            }
        }else {
            //减少的是套餐
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart setmeal = shoppingCartService.getOne(queryWrapper);
            if(setmeal.getNumber()>1){
                setmeal.setNumber(setmeal.getNumber()-1);
                cart = setmeal;
                shoppingCartService.updateById(setmeal);
            }else {
                shoppingCartService.remove(queryWrapper);
            }
        }
        return R.success(cart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        log.info("查看购物车");
        Long currentId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        Long currentId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

}
