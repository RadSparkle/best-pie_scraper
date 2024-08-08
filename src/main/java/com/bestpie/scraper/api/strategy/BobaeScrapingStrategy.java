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

@Component
public class BobaeScrapingStrategy extends ScrapingStrategy{
    private final ScrapingConfig scrapingConfig;

    private final static String BOBAE = "BOBAE";

    public BobaeScrapingStrategy(ScrapingServiceImpl scrapingService, RedisTemplate<String, String> redisTemplate, ScrapingConfig scrapingConfig) {
        super(scrapingService, redisTemplate);
        this.scrapingConfig = scrapingConfig;
    }

    public void scrape() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getBobaeBestUrl()).select(scrapingConfig.getBobaePostListCssQuery()).select("tbody").select("tr");
        for(Element element : elements) {
            String url = scrapingConfig.getBobaeHomeUrl() + element.select(scrapingConfig.getBobaeUrlCssQuery()).attr("href");
            String title = element.select(scrapingConfig.getBobaeTitleCssQuery()).text();

            BestPost bestPost = createBestPost(url, title, BOBAE);

            //게시글 상세내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.selectFirst("div.bodyCont");
            String content = contentElement.text();

            //DB 저장 및 Kafka로 데이터 송신
            save(bestPost, content);
        }
    }
}
