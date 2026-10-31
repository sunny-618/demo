package com.glz.spring.helloworld.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.glz.spring.helloworld.entity.User;
import com.glz.spring.helloworld.mapper.UserMapper;
import com.glz.spring.helloworld.service.WeatherAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeatherAgentService weatherAgentService;

    @Autowired
    private CompiledGraph weatherPipelineGraph;

    @GetMapping("/get_user")
    public String getUser(@RequestParam int id){
        System.out.println("===== 开始调用 Mapper =====");
        List<User> user = userMapper.selectAll();
        System.out.println("===== 查询结果: " + user + " =====");
        return  "ok";
    }

    @GetMapping("send_mail")
    public String senMail(@RequestParam long uid){
        Map<String,Object> state = new HashMap<>();
        state.put("userId",uid);
        Optional<OverAllState> result = weatherPipelineGraph.invoke(state);
        return  "发送成功";
//       return weatherAgentService.executeTask(uid);
    }
}
