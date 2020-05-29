package com.coder520.mamabike.fee.dao;

import com.coder520.mamabike.fee.entity.RideFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface RideFeeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RideFee record);

    int insertSelective(RideFee record);

    RideFee selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RideFee record);

    int updateByPrimaryKey(RideFee record);

    RideFee selectBikeTypeFee(Byte type);
}