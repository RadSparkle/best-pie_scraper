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
public class ClienScrapingStrategy extends ScrapingStrategy {
    private final ScrapingConfig scrapingConfig;

    private final static String CLIEN = "CLIEN";


    public ClienScrapingStrategy(ScrapingServiceImpl scrapingService, RedisTemplate<String, String> redisTemplate, ScrapingConfig scrapingConfig) {
        super(scrapingService, redisTemplate);
        this.scrapingConfig = scrapingConfig;
    }

    public void scrape() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getClienBestUrl()).select(scrapingConfig.getClienPostListCssQuery());

        for(Element element : elements) {
            String url = scrapingConfig.getClienHomeUrl() + URLDecoder.decode(element.select(scrapingConfig.getClienUrlCssQuery()).attr("href"), StandardCharsets.UTF_8);
            String title = element.select(scrapingConfig.getClienTitleCssQuery()).attr("title");

            BestPost bestPost = createBestPost(url, title, CLIEN);

            //게시글 제목이 없을시 처리안하고 다음 게시글로
            if (bestPost.getTitle().isEmpty()) continue;

            //게시글 상세내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.selectFirst("div.post_content");
            String content = contentElement.text();

            //게시글 DB 저장 및 Kafka로 송신
            save(bestPost, content);
        }
    }
}
