package model;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/23
 */
public enum CustomExceptionType {
    HTTP(1101),
    NO_DATA(1102);

    CustomExceptionType(int code) {
        this.code = code;
    }
    private final int code;

    public int getCode() {
        return code;
    }
}
