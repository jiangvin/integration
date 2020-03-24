package model;

/**
 * @author 蒋文龙(Vin)
 * @description 自定义业务异常
 * @date 2019/11/12
 */


public class CustomException extends RuntimeException {

    public CustomException(CustomExceptionType type, String msg) {
        super(String.format("%d(%s) : %s", type.getCode(), type.name(), msg));
    }
}
