package com.hitwh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hitwh.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
