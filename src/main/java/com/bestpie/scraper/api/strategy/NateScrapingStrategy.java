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
public class NateScrapingStrategy extends ScrapingStrategy {

    private final ScrapingConfig scrapingConfig;

    private final static String NATE = "NATE";


    public NateScrapingStrategy(ScrapingServiceImpl scrapingService, RedisTemplate<String, String> redisTemplate, ScrapingConfig scrapingConfig) {
        super(scrapingService, redisTemplate);
        this.scrapingConfig = scrapingConfig;
    }

    public void scrape() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getNateBestUrl()).select(scrapingConfig.getNatePostListCssQuery()).select("li");
        for(Element element : elements) {
            String url = scrapingConfig.getNateHomeUrl() + URLDecoder.decode(element.select("a").attr("href"), StandardCharsets.UTF_8);
            String title = element.select("h2").text();

            BestPost bestPost = createBestPost(url, title, NATE);

            //게시글 상세 내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.getElementById("contentArea");
            String content = contentElement.text();

            // DB 저장 및 kafka 로 송신
            save(bestPost, content);
        }
    }
}
