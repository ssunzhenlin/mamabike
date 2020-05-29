package com.coder520.mamabike.bike.dao;

import com.coder520.mamabike.bike.entity.Bike;
import com.coder520.mamabike.bike.entity.BikeNoGen;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface BikeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Bike record);

    int insertSelective(Bike record);

    Bike selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Bike record);

    int updateByPrimaryKey(Bike record);

    /**
     * 生成唯一单车编号sql
     * @param bikeNoGen
     */
    void generateBikeNo(BikeNoGen bikeNoGen);

    Bike selectByBikeNo(Long bikeNo);
}