import java.util.Map;

/**
 * @author 蒋文龙(Vin)
 * @className AgentApplication
 * @description
 * @date 2019/8/29
 */
public class AgentApplication {
    public static void main(String[] args) {
        System.out.println("显示所有环境变量:");
        int index = 0;
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(String.format("%d:%s=%s", ++index, entry.getKey(), entry.getValue()));
        }
    }
}
