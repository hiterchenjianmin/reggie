package com.hitwh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.hitwh.reggie.common.BaseContext;
import com.hitwh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",request.getRequestURI());
//定义不需要处理的请求路径
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
           "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(requestURI,urls);

        if(check){
            log.info("本次请求{}不需要处理",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }
//如果已经登陆，放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登陆,用户id为{}",request.getSession().getAttribute("employee"));

            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentid(empId);

            Long id = Thread.currentThread().getId();
            log.info("当前线程id为{}",id);

            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已经登陆,用户id为{}",request.getSession().getAttribute("user"));

            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentid(userId);

            Long id = Thread.currentThread().getId();
            log.info("当前线程id为{}",id);

            filterChain.doFilter(request,response);
            return;
        }


        //还没有登录,通过输出流形式像客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录");
        return;
//        log.info("拦截到请求: {}",request.getRequestURI());
//        filterChain.doFilter(request,response);

    }

    /**
     * 路径匹配，检测是否需要放行
     * @return
     */
    public boolean check(String requestURI,String[] urls){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match==true){
                return true;
            }
        }
        return false;
    }
}
