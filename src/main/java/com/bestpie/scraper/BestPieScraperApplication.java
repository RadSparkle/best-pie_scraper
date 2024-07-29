package com.bestpie.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BestPieScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(BestPieScraperApplication.class, args);
    }

}
