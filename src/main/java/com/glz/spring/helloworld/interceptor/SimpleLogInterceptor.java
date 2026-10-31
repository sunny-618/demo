package com.glz.spring.helloworld.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.net.http.HttpResponse;

@Component
public class SimpleLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object object) throws  Exception{
        System.out.println("【preHandle】请求路径："  + request.getRequestURI());

//        String token = request.getHeader("token");
//        if(token == null ||token.isEmpty()){
//            System.out.println("【preHandle】token 为空，拦截请求");
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先获取 token\"}");
//            return false;
//        }
        System.out.println("【preHandle】token 校验通过，放行");
        return  true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("【postHandle】目标方法执行完毕");
    }

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception{
        System.out.println("【afterCompletion】请求处理完成，异常信息：" + (ex == null ? "无" : ex.getMessage()));
    }
}
