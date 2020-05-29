package com.coder520.mamabike.bike.service;

import com.coder520.mamabike.bike.entity.BikeLocation;
import com.coder520.mamabike.common.exception.MaMaBikeException;
import com.coder520.mamabike.user.entity.UserElement;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/6.
 */
public interface BikeService {

    void generateBike() throws MaMaBikeException;

    void unLockBike(UserElement currentUser,Long bikeNo)throws MaMaBikeException;

    void lockBike( BikeLocation bikeLocation)throws MaMaBikeException;

    void reportLocation(BikeLocation bikeLocation)throws MaMaBikeException;
}
