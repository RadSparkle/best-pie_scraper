package com.bestpie.scraper.api.service;

import com.bestpie.scraper.common.entity.BestPost;

public interface ScrapingService {

    Long savePost(BestPost bestPost);
}
