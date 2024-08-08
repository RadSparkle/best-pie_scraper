package com.bestpie.scraper.api.controller;

import com.bestpie.scraper.api.strategy.ScrapingStrategy;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
@Log4j2
public class ScrapingController {
    private final List<ScrapingStrategy> scrapingStrategies;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Scheduled(fixedRate = 60000)
    public void scrapeAllSites() {
        for (ScrapingStrategy strategy : scrapingStrategies) {
            executorService.submit(strategy::scrape);
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }
}
