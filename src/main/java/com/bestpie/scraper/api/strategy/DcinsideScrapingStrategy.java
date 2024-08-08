package com.bestpie.scraper.api.strategy;

import com.bestpie.scraper.api.service.ScrapingServiceImpl;
import com.bestpie.scraper.common.entity.BestPost;
import com.bestpie.scraper.common.utils.ScrapeUtil;
import com.bestpie.scraper.config.ScrapingConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class DcinsideScrapingStrategy extends ScrapingStrategy{
    private final ScrapingConfig scrapingConfig;
    private final static String DCINSIDE = "DCINSIDE";

    public DcinsideScrapingStrategy(ScrapingServiceImpl scrapingService, RedisTemplate<String, String> redisTemplate, ScrapingConfig scrapingConfig) {
        super(scrapingService, redisTemplate);
        this.scrapingConfig = scrapingConfig;
    }

    public void scrape() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getDcinsideBestUrl()).select(scrapingConfig.getDcinsidePostListCssQuery());
        for(Element element : elements) {
            String url =scrapingConfig.getDcinsideHomeUrl() + URLDecoder.decode(element.select("a").attr("href"), StandardCharsets.UTF_8);
            String title = element.selectFirst("a").text();

            BestPost bestPost = createBestPost(url, title, DCINSIDE);

            //게시글 상세내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.selectFirst("div.write_div");
            String content = contentElement.text();
            //DB 저장 및 Kafka로 전송
            save(bestPost, content);
        }
    }
}
