/*
 * Copyright (c) 2011-2025 PiChen
 */

package com.github.peterchenhdu.future.tool.mb.crawler4j.service.news.impl;

import com.github.peterchenhdu.future.common.enums.CalendarFieldEnum;
import com.github.peterchenhdu.future.tool.mb.crawler4j.common.base.BaseService;
import com.github.peterchenhdu.future.tool.mb.crawler4j.dao.INewsDao;
import com.github.peterchenhdu.future.tool.mb.crawler4j.model.News;
import com.github.peterchenhdu.future.tool.mb.crawler4j.service.news.INewsService;
import com.github.peterchenhdu.future.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("newsService")
public class NewsServiceImpl extends BaseService implements INewsService {

    @Autowired
    private INewsDao newsDao;

    @Override
    public News saveNews(News news) {
        String contTableName = this.getContTableNameByTime(news.getPublishTime());
        String sumTableName = this.getSumTableNameByTime(news.getPublishTime());
        //不存在，则创建表
        newsDao.createNewTableCont(contTableName);
        newsDao.createNewTableSum(sumTableName);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("url", news.getUrl());
        param.put("title", news.getTitle());
        param.put("contentText", news.getContentText());
        param.put("contentHtml", news.getContentHtml());
        param.put("srcUrl", news.getSrcUrl());
        param.put("srcName", news.getSrcName());
        param.put("publishTime", news.getPublishTime());
        param.put("crawlerSrc", news.getCrawlerSrc());
        param.put("tableName", contTableName);
        newsDao.saveNewsCont(param);
        param.put("tableName", sumTableName);
        newsDao.saveNewsSum(param);
        return news;
    }

    @Override
    public List<News> findByNews(News news) {
        String contTableName = this.getContTableNameByTime(news.getPublishTime());
        String sumTableName = this.getSumTableNameByTime(news.getPublishTime());
        //不存在，则创建表
        newsDao.createNewTableCont(contTableName);
        newsDao.createNewTableSum(sumTableName);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("url", news.getUrl());
        param.put("tableName", contTableName);
        return newsDao.findByNews(param);
    }

    private String getContTableNameByTime(String pubTime) {
        String time = pubTime.replace("-", "_");
        return "is_news_cont_" + time.subSequence(0, 7);
    }

    private String getSumTableNameByTime(String pubTime) {
        return "is_news_sum_" + pubTime.subSequence(0, 4);
    }

    @Override
    public long getYearCount(int year) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tableName", "is_news_sum_" + year);
        long count = 0;
        try {
            count = newsDao.getCount(param);
        } catch (BadSqlGrammarException e) {
            logger.warn("Bad Sql Grammar Exception, this often happened when table is_news_sum_{} does not exist.", year);
            logger.error(e.toString(), e);
        }

        return count;
    }

    @Override
    public List<News> findByMonth(String month, long offset, long limit) {
        String tableName = this.getSumTableNameByTime(month);
        String from = month + "-01 00:00:00";
        String to = DateTimeUtils.add(from, CalendarFieldEnum.MONTH, 1);
        List<News> rst = new ArrayList<>();

        try {
            rst = newsDao.findByTime(tableName, from, to, offset, limit);
        } catch (BadSqlGrammarException e) {
            logger.warn("Bad Sql Grammar Exception, this often happened when table is_news_sum_{} does not exist.", month.subSequence(0, 4));
            logger.error(e.toString(), e);
        }

        for (News news : rst) {
            switch (news.getCrawlerSrc()) {
                case "news.163.com":
                    news.setCrawlerName("网易新闻");
                    break;

                default:
                    break;
            }
        }
        return rst;
    }


    @Override
    public long getCount(String month) {
        String tableName = this.getContTableNameByTime(month);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tableName", tableName);
        long count = 0;
        try {
            count = newsDao.getCount(param);
        } catch (BadSqlGrammarException e) {
            logger.warn("Bad Sql Grammar Exception, this often happened when table is_news_cont_{} does not exist.", month.subSequence(0, 7));
            logger.error(e.toString(), e);
        }
        return count;
    }

}
