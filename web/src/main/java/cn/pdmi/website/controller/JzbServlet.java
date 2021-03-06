package cn.pdmi.website.controller;

import cn.pdmi.modianSpider.core.JzbSpider;
import cn.pdmi.modianSpider.pojo.DataModel;
import cn.pdmi.modianSpider.utils.HttpSpiderUtils;
import cn.pdmi.website.service.DataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_ on 2018/4/26.
 */
public class JzbServlet extends HttpServlet {
    private DataService dataService = new DataService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JzbSpider jzbSpider = new JzbSpider();
        String json = jzbSpider.getJson();
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
