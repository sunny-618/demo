package com.glz.spring.helloworld.tool;

import com.glz.spring.helloworld.entity.User;
import com.glz.spring.helloworld.mapper.UserMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataBaseQueryTool {


    @Autowired
    private UserMapper userMapper;

    @Tool(description = "根据用户名查询用户信息，返回用户 ID、用户名、邮箱等字段")
    public User queryUserByName(
            @ToolParam(description = "要查询的用户名") String username) {
        return userMapper.selectByName(username);
    }


    @Tool(description = "根据用户 ID 查询用户详情")
    public User executeSelectQuery(long userId) {
      return userMapper.selectById(userId);
    }
}
