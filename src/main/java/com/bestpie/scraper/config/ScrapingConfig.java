package com.bestpie.scraper.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ScrapingConfig {
    @Value("${scraping.best.url.dcinside}")
    private String dcinsideBestUrl;

    @Value("${scraping.best.url.clien}")
    private String clienBestUrl;

    @Value("${scraping.best.url.nate}")
    private String nateBestUrl;

    @Value("${scraping.best.url.bobae}")
    private String bobaeBestUrl;

    @Value("${scraping.home.url.dcinside}")
    private String dcinsideHomeUrl;

    @Value("${scraping.home.url.clien}")
    private String clienHomeUrl;

    @Value("${scraping.home.url.nate}")
    private String nateHomeUrl;

    @Value("${scraping.home.url.bobae}")
    private String bobaeHomeUrl;

    @Value("${scraping.css_query.dcinside.post_list}")
    private String dcinsidePostListCssQuery;

    @Value("${scraping.css_query.dcinside.url}")
    private String dcinsideUrlCssQuery;

    @Value("${scraping.css_query.clien.post_list}")
    private String clienPostListCssQuery;

    @Value("${scraping.css_query.clien.url}")
    private String clienUrlCssQuery;

    @Value("${scraping.css_query.clien.title}")
    private String clienTitleCssQuery;

    @Value("${scraping.css_query.nate.post_list}")
    private String natePostListCssQuery;

    @Value("${scraping.css_query.bobae.post_list}")
    private String bobaePostListCssQuery;

    @Value("${scraping.css_query.bobae.url}")
    private String bobaeUrlCssQuery;

    @Value("${scraping.css_query.bobae.title}")
    private String bobaeTitleCssQuery;
}