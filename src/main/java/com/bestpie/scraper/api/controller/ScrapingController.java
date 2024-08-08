package com.bestpie.scraper.api.controller;

import com.bestpie.scraper.api.service.ScrapingServiceImpl;
import com.bestpie.scraper.common.entity.BestPost;
import com.bestpie.scraper.common.utils.SSL;
import com.bestpie.scraper.common.utils.ScrapeUtil;
import com.bestpie.scraper.config.ScrapingConfig;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
@Log4j2
public class ScrapingController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ScrapingServiceImpl scrapingService;

    private SSL ssl;

    private ScrapingConfig scrapingConfig;

    private final static String DCINSIDE = "DCINSIDE";

    private final static String CLIEN = "CLIEN";

    private final static String NATE = "NATE";

    private final static String BOBAE = "BOBAE";

    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // 스레드 풀을 생성

    @Scheduled(fixedRate = 600000)
    public void scrapeAllSites() {
        executorService.submit(this::dcinsideScraping);
        executorService.submit(this::clienScraping);
        executorService.submit(this::natePanScraping);
        executorService.submit(this::bobaeScraping);
    }

    public void dcinsideScraping() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getDcinsideBestUrl()).select(scrapingConfig.getDcinsidePostListCssQuery());
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(scrapingConfig.getDcinsideHomeUrl() + URLDecoder.decode(element.select("a").attr("href"), StandardCharsets.UTF_8));
            bestPost.setTitle(element.selectFirst("a").text());
            bestPost.setSiteName(DCINSIDE);

            //게시글 상세내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.selectFirst("div.write_div");
            String content = contentElement.text();
            //DB 저장 및 Kafka로 전송
            save(bestPost, content);

        }
    }

    public void clienScraping() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getClienBestUrl()).select(scrapingConfig.getClienPostListCssQuery());

        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(scrapingConfig.getClienHomeUrl() + URLDecoder.decode(element.select(scrapingConfig.getClienUrlCssQuery()).attr("href"), StandardCharsets.UTF_8));
            bestPost.setTitle(element.select(scrapingConfig.getClienTitleCssQuery()).attr("title"));
            bestPost.setSiteName(CLIEN);

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

    public void natePanScraping() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getNateBestUrl()).select(scrapingConfig.getNatePostListCssQuery()).select("li");
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            String url = scrapingConfig.getNateHomeUrl() + URLDecoder.decode(element.select("a").attr("href"), StandardCharsets.UTF_8);
            bestPost.setUrl(url);
            bestPost.setTitle(element.select("h2").text());
            bestPost.setSiteName(NATE);

            //게시글 상세 내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.getElementById("contentArea");
            String content = contentElement.text();

            // DB 저장 및 kafka 로 송신
            save(bestPost, content);
        }
    }

    public void bobaeScraping() {
        Elements elements = ScrapeUtil.getWebPage(scrapingConfig.getBobaeBestUrl()).select(scrapingConfig.getBobaePostListCssQuery()).select("tbody").select("tr");
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(scrapingConfig.getBobaeHomeUrl() + element.select(scrapingConfig.getBobaeUrlCssQuery()).attr("href"));
            bestPost.setTitle(element.select(scrapingConfig.getBobaeTitleCssQuery()).text());
            bestPost.setSiteName(BOBAE);

            //게시글 상세내용 가져오기
            Document document = ScrapeUtil.getWebPage(bestPost.getUrl());
            Element contentElement = document.selectFirst("div.bodyCont");
            String content = contentElement.text();

            //DB 저장 및 Kafka로 데이터 송신
            save(bestPost, content);
        }
    }

    public void save(BestPost bestPost, String content) {
        Long id = scrapingService.savePost(bestPost);
        if (id!=null) redisTemplate.opsForValue().set(id.toString(), content);
    }

    @PreDestroy
    public void shutdownExcutorService() {
        executorService.shutdown();
    }
}
