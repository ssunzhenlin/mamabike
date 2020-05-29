package com.coder520.mamabike.user.dao;

import com.coder520.mamabike.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 根据手机号码查找用户
     * @param mobile
     * @return
     */
    User selectByMobile(String mobile);
}