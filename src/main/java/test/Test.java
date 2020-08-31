package test;

import util.UrlTestUtil;

/**
 * @program: url-test-migration
 * @description:
 * @author: tmz
 * @create: 2020-08-31 14:16
 */
public class Test {
    public static void main(String[] args) {
        UrlTestUtil.urlTestThreadLine("D:\\tenlentcloud\\mp3.txt"
                ,"C:\\Users\\tang\\Desktop\\a1.txt",
                "https://ask-1252162195.cos.ap-guangzhou.myqcloud.com",
                "C:\\Users\\tang\\Desktop\\a2.txt");
    }
}
