package com.integration.provider.manager;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author 蒋文龙(Vin)
 * @date 2018/10/10
 */

public class DictionaryTreeManager {

    @Data
    class Node {
        private String key;
        private boolean isWord;
        private int count = 0;

        private Node parent;
        private Map<String, Node> children = new HashMap<>();

        Node(String key, Node parent, boolean isWord) {
            this.key = key;
            this.parent = parent;
            this.isWord = isWord;
            if (isWord) {
                ++count;
            }
        }

        Node(String key) {
            this(key, null, true);
        }

        Node(String key, Node parent) {
            this(key, parent, true);
        }

        void addWord(String word) {
            if (word.equals(key)) {
                if (!isWord) {
                    isWord = true;
                    addCount(1);
                }
                return;
            }

            int prefixCount = equalPrefixCount(key, word);
            if (prefixCount == key.length()) {
                //key为前缀,新塞子节点
                String newStr = word.substring(prefixCount);
                if (children.containsKey(newStr) && children.get(newStr).isWord()) {
                    return;
                }

                Map.Entry<String, Node> find =
                    children.entrySet().stream()
                    .filter(entry -> equalPrefixCount(entry.getKey(), newStr) != 0)
                    .findFirst().orElse(null);

                if (find != null) {
                    find.getValue().addWord(newStr);
                } else {
                    addChild(new Node(newStr, this));
                    addCount(1);
                }
            } else if (prefixCount == word.length()) {
                //word为前缀，新增父节点
                String oldKey = this.key;
                this.key = key.substring(prefixCount);

                Node newParent = new Node(word, this.parent);
                newParent.addChild(this);
                newParent.setCount(this.count + 1);

                this.parent.removeChild(oldKey);
                this.parent.addChild(newParent);
                this.parent.addCount(1);
                this.parent = newParent;
            } else {
                //全新前缀,全新父节点
                String oldKey = this.key;
                this.key = key.substring(prefixCount);

                String newWord = word.substring(prefixCount);
                Node newWordNode = new Node(newWord);

                String newPrefix = oldKey.substring(0, prefixCount);
                Node newParent = new Node(newPrefix, this.parent, false);
                newParent.setCount(this.count + 1);
                newParent.addChild(this);
                newParent.addChild(newWordNode);
                newWordNode.setParent(newParent);

                this.parent.removeChild(oldKey);
                this.parent.addChild(newParent);
                this.parent.addCount(1);
                this.parent = newParent;
            }
        }

        void addChild(Node node) {
            children.put(node.key, node);
        }

        void removeChild(String key) {
            children.remove(key);
        }

        boolean checkSelf() {
            return count == recalculateCount();
        }

        String toText(String prefix) {
            StringBuilder str = new StringBuilder(prefix + key + "," + count + "," + isWord + "\r\n");
            children.entrySet().stream().forEach(entry -> {
                str.append(entry.getValue().toText("    " + prefix + key));
            });
            return str.toString();
        }

        private int equalPrefixCount(String str1, String str2) {
            String longStr;
            String shortStr;
            if (str1.length() > str2.length()) {
                longStr = str1;
                shortStr = str2;
            } else {
                longStr = str2;
                shortStr = str1;
            }

            for (int i = 0; i < shortStr.length(); ++i) {
                if (shortStr.charAt(i) != longStr.charAt((i))) {
                    return i;
                }
            }
            return shortStr.length();
        }

        private int recalculateCount() {
            int count = 0;
            for (Map.Entry<String, Node> child : children.entrySet()) {
                count += child.getValue().recalculateCount();
            }
            if (isWord) {
                ++count;
            }
            return count;
        }

        private void addCount(int add) {
            count += add;
            if (parent != null) {
                parent.addCount(add);
            }
        }
    }

    private Node root = new Node("", null, false);

    public void addWord(String word) {
        if (StringUtils.isEmpty(word)) {
            return;
        }
        root.addWord(word);
    }

    public int count() {
        return root.count;
    }

    public boolean checkSelf() {
        return root.checkSelf();
    }

    public String toText() {
        return root.toText("") + "\r\n";
    }
}
