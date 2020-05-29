package com.coder520.mamabike.common.resp;

import com.coder520.mamabike.common.constants.Constants;
import lombok.Data;

/**
 * Created by JackWangon[www.] 2017/7/29.
 */
@Data
public class ApiResult<T> {

    private int code = Constants.RESP_STATUS_OK;

    private String message;

    private T data;


}
