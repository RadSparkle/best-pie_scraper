package com.bestpie.scraper.api.strategy;

import com.bestpie.scraper.api.service.ScrapingServiceImpl;
import com.bestpie.scraper.common.entity.BestPost;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public abstract class ScrapingStrategy {
    private final ScrapingServiceImpl scrapingService;

    private final RedisTemplate<String, String> redisTemplate;

    ScrapingStrategy(ScrapingServiceImpl scrapingService, RedisTemplate<String, String> redisTemplate) {
        this.scrapingService = scrapingService;
        this.redisTemplate = redisTemplate;
    }
    public abstract void scrape();

    BestPost createBestPost(String url, String title, String siteName) {
        BestPost bestPost = new BestPost();
        bestPost.setUrl(url);
        bestPost.setTitle(title);
        bestPost.setSiteName(siteName);
        return bestPost;
    }

    void save(BestPost bestPost, String content) {
        Long id = scrapingService.savePost(bestPost);
        if (id!=null) redisTemplate.opsForValue().set(id.toString(), content);
    }
}
