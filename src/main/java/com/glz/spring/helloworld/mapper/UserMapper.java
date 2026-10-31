package com.glz.spring.helloworld.mapper;

import com.glz.spring.helloworld.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id,name,email,address FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT id,name,email FROM user WHERE name = #{name}")
    User selectByName(String name);

    @Select("SELECT id,name FROM user")
    List<User> selectAll();


    @Select("SELECT COUNT(*) FROM user")
    int countUsers();
}
