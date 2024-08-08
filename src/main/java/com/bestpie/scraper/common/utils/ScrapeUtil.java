package com.bestpie.scraper.common.utils;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Component
@Log4j2
public class ScrapeUtil {
    private static SSL ssl = new SSL();

    public static Document getWebPage(String url) {
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
