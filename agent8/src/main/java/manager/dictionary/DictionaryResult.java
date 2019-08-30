package manager.dictionary;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author 蒋文龙(Vin)
 * @date 2018/10/15
 */

@Data
public class DictionaryResult {
    private int totalCount = 0;
    private List<String> topResultList = new ArrayList<>();
    private int findTime = 0;
    private boolean findFlag = false;

    public void addFindTime() {
        ++findTime;
    }
}
