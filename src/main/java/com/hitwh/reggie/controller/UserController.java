package com.hitwh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hitwh.reggie.common.R;
import com.hitwh.reggie.entity.User;
import com.hitwh.reggie.service.UserService;
import com.hitwh.reggie.utils.SMSUtils;
import com.hitwh.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成四位号码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);
            //调用阿里云api
           // SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            // 需要将生成的验证码保存起来
            session.setAttribute(phone,code);
            return R.success("手机验证码短信验证成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
       log.info("map:{}",map);
       //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        session.setAttribute("phone",code);
        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute("phone");

        //进行验证码比对
        if(codeInSession!=null&&codeInSession.equals(code)){
            //判断手机号是否在用户表里面，如果是新用户，自动完成注册
            LambdaQueryWrapper<User> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User one = userService.getOne(queryWrapper);
            if (one == null){
                one = new User();
                one.setPhone(phone);
                one.setStatus(1);
                userService.save(one);
            }
            session.setAttribute("user",one.getId());
            log.info("id为{}",one.getId());
            return R.success(one);
        }

        return R.error("登录失败");

    }
}
