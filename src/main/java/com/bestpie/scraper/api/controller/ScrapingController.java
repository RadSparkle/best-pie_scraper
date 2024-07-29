package com.bestpie.scraper.api.controller;

import com.bestpie.scraper.api.service.ScrapingServiceImpl;
import com.bestpie.scraper.common.entity.BestPost;
import com.bestpie.scraper.common.utils.SSL;
import com.bestpie.scraper.config.ScrapingConfig;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
@Log4j2
public class ScrapingController {

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
        Elements elements = getWebPage(scrapingConfig.getDcinsideBestUrl()).select(scrapingConfig.getDcinsidePostListCssQuery());
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(URLDecoder.decode(element.select(scrapingConfig.getDcinsideUrlCssQuery()).attr("href"), StandardCharsets.UTF_8));
            bestPost.setTitle(element.selectFirst("a").text());
            bestPost.setSiteName(DCINSIDE);
            scrapingService.savePost(bestPost);
        }
    }

    public void clienScraping() {
        Elements elements = getWebPage(scrapingConfig.getClienBestUrl()).select(scrapingConfig.getClienPostListCssQuery());

        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(scrapingConfig.getClienHomeUrl() + URLDecoder.decode(element.select(scrapingConfig.getClienUrlCssQuery()).attr("href"), StandardCharsets.UTF_8));
            bestPost.setTitle(element.select(scrapingConfig.getClienTitleCssQuery()).attr("title"));
            bestPost.setSiteName(CLIEN);
            if (bestPost.getTitle().isEmpty()) continue;
            scrapingService.savePost(bestPost);
        }
    }

    public void natePanScraping() {
        Elements elements = getWebPage(scrapingConfig.getNateBestUrl()).select(scrapingConfig.getNatePostListCssQuery()).select("li");
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            String url = scrapingConfig.getNateHomeUrl() + URLDecoder.decode(element.select("a").attr("href"), StandardCharsets.UTF_8);
            bestPost.setUrl(url);
            bestPost.setTitle(element.select("h2").text());
            bestPost.setSiteName(NATE);
            scrapingService.savePost(bestPost);
        }
    }

    public void bobaeScraping() {
        Elements elements = getWebPage(scrapingConfig.getBobaeBestUrl()).select(scrapingConfig.getBobaePostListCssQuery()).select("tbody").select("tr");
        for(Element element : elements) {
            BestPost bestPost = new BestPost();
            bestPost.setUrl(scrapingConfig.getBobaeHomeUrl() + element.select(scrapingConfig.getBobaeUrlCssQuery()).attr("href"));
            bestPost.setTitle(element.select(scrapingConfig.getBobaeTitleCssQuery()).text());
            bestPost.setSiteName(BOBAE);
            scrapingService.savePost(bestPost);
        }
    }
    @PreDestroy
    public void shutdownExcutorService() {
        executorService.shutdown();
    }

    public Document getWebPage(String url) {
        try {
            ssl.setSSL();
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Can't get web page : {}, {}", url, e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
