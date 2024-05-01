package com.hitwh.reggie.dto;

import com.hitwh.reggie.entity.Setmeal;
import com.hitwh.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
