package com.glz.spring.helloworld.interceptor;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.apache.ibatis.executor.Executor;

@Component
@Order(1)
@Intercepts({
        @Signature(type = Executor.class,method = "query",
        args = {MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisInterceptor implements Interceptor {

    // 拦截的核心逻辑，在这里写要干什么
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("===== Mybatis拦截器开始 =====");

        Object result = invocation.proceed();

        System.out.println("===== Mybatis拦截器结束 =====");
        return result;
    }
}
