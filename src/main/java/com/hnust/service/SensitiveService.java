package com.hnust.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤类
     * 实现了 InitializingBean 接口，就可以在 afterPropertiesSet() 方法执行后把敏感词文本都读取进来
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "***";      //默认敏感词替换符

    /**
     * 定义前缀树
     */
    private class TreeNode {
        private boolean end = false;    //是否是某个敏感词的结尾
        //当前节点下所有的子节点的集合（key:子结点的键；value:子节点）
        private Map<Character, TreeNode> subNodes = new HashMap<Character, TreeNode>();

        /**
         * 添加子结点
         */
        void addSubNode(Character key, TreeNode node) {
            subNodes.put(key, node);
        }

        /**
         * 获取下个节点
         */
        TreeNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        /**
         * 是否敏感词的结尾字符
         */
        boolean isKeywordEnd() {
            return end;
        }

        /**
         * 标记节点为敏感词的结尾字符
         */
        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }

    private TreeNode rootNode = new TreeNode();     //根节点

    /**
     * 判断字符是否属于东亚文字范围
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * 过滤评论中的敏感词
     * @param text：待过滤的文本
     * @return ： 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {    //若 text 为空
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;   //敏感词的替代字符
        StringBuilder result = new StringBuilder();
        TreeNode tempNode = rootNode;   //当前节点，初始化为根节点
        int begin = 0;          //
        int position = 0;       //当前比较的位置
        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {  //c 是不合法字符
                if (tempNode == rootNode) {
                    result.append(c);   //添加正常的空格到text
                    ++begin;
                }
                ++position;     //跳过夹在敏感词字符中间的空格
                continue;
            }
            tempNode = tempNode.getSubNode(c);      //找当前节点的子节点
            if (tempNode == null) {     //没有字符是 c 的子节点，那么说明以 begin 开头的字符串不可能是敏感词
                result.append(text.charAt(begin));  //将begin位置的字符添加到result中
                //跳到下一个字符开始判断
                position = begin + 1;
                begin = position;
                //回到树的根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {   //有字符是 c 的子节点，且字符 c 是敏感词结尾的子节点，说明找到敏感词
                // 发现敏感词，那么从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {    //有字符是 c 的子节点，但字符 c 不是敏感词结尾的子节点
                ++position;
            }
        }
        result.append(text.substring(begin));   //将text中最后一个字符添加到result
        return result.toString();
    }

    /**
     * 添加敏感词到前缀树
     * @param lineTxt：要添加的敏感词
     */
    private void addWord(String lineTxt) {
        TreeNode tempNode = rootNode;       //根节点
        // 循环每个字符
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);    //敏感词的第 i 个字符 c
            // 过滤空格
            if (isSymbol(c)) {  //过滤敏感词中的不合法字符
                continue;
            }
            TreeNode node = tempNode.getSubNode(c);     //找当前节点下的节点 c
            if (node == null) { // 没有子节点 c
                node = new TreeNode();      //新建节点
                tempNode.addSubNode(c, node);   //将新建的节点 node(其 key 为 c) 添加为当前节点的子节点
            }
            tempNode = node;    //当前节点为子节点 node
            if (i == lineTxt.length() - 1) {    //如果遍历到了敏感词的最后一个字符
                tempNode.setKeywordEnd(true);   //将该节点标记为敏感词的结尾字符
            }
        }
    }


    /**
     * 此方法将在所有的属性被初始化后调用
     * afterPropertiesSet 必须实现 InitializingBean 接口。实现 InitializingBean 接口必须实现afterPropertiesSet方法。
     *
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TreeNode();      //根节点
        try {
            //读取敏感词文件
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            //一行行读取文本
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);       //添加敏感词 lineTxt 到前缀树
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }
//
//    public static void main(String[] args) {
//        SensitiveService s = new SensitiveService();
//        s.addWord("色情");
//        s.addWord("赌博");
//        System.out.print(s.filter("hi 你好色 情"));
//    }
}
