package com.integration.provider.manager.dictionary;

import com.integration.provider.domain.DictionaryResult;
import org.apache.commons.lang.StringUtils;

/**
 * @author 蒋文龙(Vin)
 * @date 2018/10/10
 */

public class DictionaryTreeManager {

    private Node root = new Node("", null, false);

    public void addWord(String word) {
        if (StringUtils.isEmpty(word)) {
            return;
        }
        root.addWord(word);
    }

    public int count() {
        return root.getCount();
    }

    public boolean checkSelf() {
        return root.checkSelf();
    }

    public String toText() {
        return root.toText("", 0) + "\r\n";
    }

    public DictionaryResult findWordWithPrefix(String prefix) {
        DictionaryResult result = new DictionaryResult();
        root.findWordWithPrefix(result, "", prefix, 10);
        return result;
    }
}
