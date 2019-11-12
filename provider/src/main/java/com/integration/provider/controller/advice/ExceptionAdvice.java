package com.integration.provider.controller.advice;

import com.integration.provider.domain.CustomException;
import com.integration.provider.domain.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2019/9/23
 */

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultData exceptionHandler(Exception e) {
        log.error("controller error:", e);
        int code;
        String msg;
        if (e instanceof CustomException) {
            code = ((CustomException) e).getCode();
            msg = ((CustomException) e).getMsg();
        } else {
            code = 1100;
            msg = e.getClass().getName() + ":" + e.getMessage();
        }
        return new ResultData(code, msg);
    }
}
