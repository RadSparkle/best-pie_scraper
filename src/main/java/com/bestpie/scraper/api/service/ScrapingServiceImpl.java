package com.bestpie.scraper.api.service;

import com.bestpie.scraper.kafka.BestPostProducer;
import com.bestpie.scraper.api.repository.ScrapingRepository;
import com.bestpie.scraper.common.entity.BestPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScrapingServiceImpl implements ScrapingService {

    private final ScrapingRepository scrapingRepository;

    @Autowired
    private BestPostProducer bestPostProducer;

    @Override
    public Long savePost(BestPost bestPost) {
        if(!scrapingRepository.existsByTitle(bestPost.getTitle())){
            BestPost savedBestPost = scrapingRepository.save(bestPost);
            log.info("Scraping completed | {} | {}", bestPost.getSiteName(), bestPost.getTitle());
            bestPostProducer.sendMessage(bestPost.getId());
            return savedBestPost.getId();
        }
        return null;
    }
}
