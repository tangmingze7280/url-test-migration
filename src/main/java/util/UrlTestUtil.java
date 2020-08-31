package util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program:
 * @description:
 * @author: tmz
 * @create: 2020-08-27 17:30
 */
public class UrlTestUtil {
    /**
     * @param host        域名前缀
     * @param outFileUrl  输出 未上传已有 url
     * @param fileUrl     导入文件
     * @param notFountUrl 源文件404输出
     * @return void
     * @author tmz
     * @description /
     * @date 17:35 2020/8/27
     */
    public static void urlTestLine(String fileUrl, String outFileUrl, String host, String notFountUrl) {
        List<String> urls = FileUtil.readLines(fileUrl, "utf-8");
        List<String> unUpdate = new CopyOnWriteArrayList<>();
        List<String> notFountUrlList = new CopyOnWriteArrayList<>();
        for (String url : urls) {
            String decode = URLUtil.decode(url, "utf-8");
            String format = StrUtil.format(host + "{}", URLUtil.getPath(decode));
            if (test(format)) {
                if (!test(decode)) {
                    unUpdate.add(url);
                } else {
                    notFountUrlList.add(url);
                }
            } else {
                System.out.println("文件存在");
            }
        }
        FileUtil.writeLines(unUpdate, outFileUrl, "utf-8");
        FileUtil.writeLines(notFountUrlList, notFountUrl, "utf-8");
    }
    /**
     * @author tmz
     * @description 测试是否报错
     * @date 14:10 2020/8/31
     * @param [url]
     * @return java.lang.Boolean
     */
    private static Boolean test(String url) {
        try {
            URL url1 = new URL(url);
            HttpURLConnection urlcon1 = (HttpURLConnection) url1.openConnection();
            if (urlcon1.getResponseCode() >= 400) {
                System.out.println("文件不存在");
                return true;
            } else {
                System.out.println("文件存在");
                return false;
            }
        } catch (Exception e) {
            System.out.print("请求失败");
            return false;
        }
    }

    public static void urlTestLine(List<String> urls, CopyOnWriteArrayList<String> unUpdate, CopyOnWriteArrayList<String> notFountUrlList,String host) {
        for (String url : urls) {
            String decode = URLUtil.decode(url, "utf-8");
            String format = StrUtil.format(host + "{}", URLUtil.getPath(decode));
            if (test(format)) {
                if (!test(decode)) {
                    unUpdate.add(url);
                } else {
                    notFountUrlList.add(url);
                }
            } else {
                System.out.println("文件存在");
            }
        }
    }
    /**
     * @author tmz
     * @description 多线程测试
     * @date 14:09 2020/8/31
     * @param fileUrl 待检测  存在url的txt文件路径
     * @param outFileUrl  未迁移url 导出本地文件路径
     * @param  host 迁移目标域名前缀
     * @param notFountUrl 源头 url 404 不存在
     * @return void
     */
    public static void urlTestThreadLine(String fileUrl, String outFileUrl, String host, String notFountUrl) {
        List<String> urls = FileUtil.readLines(fileUrl, "utf-8");
        CopyOnWriteArrayList<String> unUpdate = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> notFountUrlList = new CopyOnWriteArrayList<>();
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(4, 32);
        try {
            getList(50,urls).forEach(e->{
                threadPoolExecutor.execute(() -> {
                    urlTestLine(e,unUpdate,notFountUrlList,host);
                });
            });
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            FileUtil.writeLines(unUpdate, outFileUrl, "utf-8");
            FileUtil.writeLines(notFountUrlList, notFountUrl, "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * @author tmz
     * @description  平分数组
     * @date 14:09 2020/8/31
     * @param [targ, mList]
     * @return java.util.List<java.util.List<java.lang.String>>
     */
    private static List<List<String>> getList(int targ,List<String> mList) {
        List<List<String>> mEndList = new ArrayList<>();
        if (mList.size() % targ != 0) {
            for (int j = 0; j < mList.size() / targ + 1; j++) {
                if ((j * targ + targ) < mList.size()) {
                    mEndList.add(mList.subList(j * targ, j * targ + targ));//0-3,4-7,8-11    j=0,j+3=3   j=j*3+1
                } else if ((j * targ + targ) > mList.size()) {
                    mEndList.add(mList.subList(j * targ, mList.size()));
                } else if (mList.size() < targ) {
                    mEndList.add(mList.subList(0, mList.size()));
                }
            }
        } else if (mList.size() % targ == 0) {
            for (int j = 0; j < mList.size() / targ; j++) {
                if ((j * targ + targ) <= mList.size()) {
                    mEndList.add(mList.subList(j * targ, j * targ + targ));//0-3,4-7,8-11    j=0,j+3=3   j=j*3+1
                } else if ((j * targ + targ) > mList.size()) {
                    mEndList.add(mList.subList(j * targ, mList.size()));
                } else if (mList.size() < targ) {
                    mEndList.add(mList.subList(0, mList.size()));
                }
            }
        }
        return mEndList;
    }
}
