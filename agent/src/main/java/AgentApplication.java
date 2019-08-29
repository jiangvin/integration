import lombok.extern.slf4j.Slf4j;

/**
 * @author 蒋文龙(Vin)
 * @className AgentApplication
 * @description
 * @date 2019/8/29
 */
@Slf4j
public class AgentApplication {
    public static void main(String[] args) {
        System.getenv().forEach((key, value) -> log.info("key: {}, value: {}", key, value));
    }
}
