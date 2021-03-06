package cn.pdmi.modianSpider.core;

import cn.pdmi.modianSpider.pojo.AndroidSearch;
import cn.pdmi.modianSpider.pojo.AppSearch;
import cn.pdmi.modianSpider.utils.DateUtils;
import cn.pdmi.modianSpider.utils.JDBCUtils;
import cn.pdmi.modianSpider.utils.KeyWordUtils;
import cn.pdmi.modianSpider.utils.SpiderUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_ on 2018/4/24.
 */
public class AndroidMarket_360_Spider implements Runnable {
    //解析网页
    public Document getDocument(String html) {
        Document document = Jsoup.parse(html);
        return document;
    }

    //封装数据模型
    public AndroidSearch getAndroidSearch(Document document, String keyWord) {
        AndroidSearch androidSearch = new AndroidSearch();
        androidSearch.setInsertDate(DateUtils.getDate());
        androidSearch.setName(keyWord);
        //对于有搜索指数的
        if (document.select("ul").get(1).select("li").get(0).select("span.red").html() != null && !"".equals(document.select("ul").get(1).select("li").get(0).select("span.red").html()) &&
                (keyWord.toLowerCase().contains(document.select("ul").get(1).select("li").get(0).select("h3 a").attr("title").toLowerCase()) || document.select("ul").get(1).select("li").get(0).select("h3 a").attr("title").toLowerCase().contains(keyWord.toLowerCase()))) {
            androidSearch.setEnter(1);
            String count = document.select("ul").get(1).select("li").get(0).select("p.downNum").html();
            if (count.endsWith("亿次下载")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("亿"))) * 1000000000));
            }
            if (count.endsWith("万次下载")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("万"))) * 10000));
            }
            if (count.endsWith("千次下载")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("千"))) * 1000));
            }
            if (count.endsWith("百次下载")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("百"))) * 100));
            }
            if (count.endsWith("十次下载")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("十"))) * 10));
            }
            if (count.endsWith("次下载") && !count.contains("亿") && !count.contains("万") && !count.contains("千") && !count.contains("百") && !count.contains("十")) {
                androidSearch.setDownloads(String.valueOf(Integer.parseInt(count.substring(0, count.indexOf("次")))));
            }
        } else {
            androidSearch.setEnter(0);
            androidSearch.setDownloads("0");
        }
        return androidSearch;
    }

    public void insert(AndroidSearch androidSearch) throws Exception {
        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "INSERT INTO androidSearch_360 (mediaName,appName,downloads,enter,insertDate) " +
                "VALUES (?,?,?,?,?)";
        int update = queryRunner.update(sql, androidSearch.getMediaName(), androidSearch.getName(), androidSearch.getDownloads(), androidSearch.getEnter(), androidSearch.getInsertDate());
        if (update == 1) {
            System.out.println("success!");
        } else {
            System.out.println("插入失败！");
        }

    }

    public String getEncode(String url) throws Exception {
        return URLEncoder.encode(url, "utf-8");
    }

    @Override
    public void run() {
        AndroidMarket_360_Spider androidMarket_360_spider = new AndroidMarket_360_Spider();
        try {
            androidMarket_360_spider.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData() throws Exception {
        AndroidMarket_360_Spider androidMarket_360_Spider = new AndroidMarket_360_Spider();
        Map<Integer, Map<String, String>> map = KeyWordUtils.getKeyWords("androidExcel");
        for (int i = 0; i < map.keySet().size(); i++) {
            Map<String, String> keyWords = map.get(i);
            for (String key : keyWords.keySet()
                    ) {
                if (!"".equals(keyWords.get(key).trim())) {
                    AndroidSearch androidSearch = androidMarket_360_Spider.getAndroidSearch(androidMarket_360_Spider.getDocument(SpiderUtils.getAjax("http://zhushou.360.cn/search/index/?kw=" + androidMarket_360_Spider.getEncode(keyWords.get(key)))),
                            keyWords.get(key));
                    androidSearch.setMediaName(key);
                    androidMarket_360_Spider.insert(androidSearch);
                } else {
                    AndroidSearch androidSearch = new AndroidSearch();
                    androidSearch.setMediaName(key);
                    androidSearch.setInsertDate(DateUtils.getDate());
                    androidMarket_360_Spider.insert(androidSearch);
                }
            }
        }
    }
}
