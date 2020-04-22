package model;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/21
 */
public enum ServiceType {
    UNKNOWN, SPRING1, SPRING2, WEB;

    public static ServiceType getServiceType(String typeStr) {
        typeStr = typeStr.toUpperCase();

        for (ServiceType serviceType : ServiceType.values()) {
            if (serviceType.toString().equals(typeStr)) {
                return serviceType;
            }
        }
        return ServiceType.UNKNOWN;
    }
}
