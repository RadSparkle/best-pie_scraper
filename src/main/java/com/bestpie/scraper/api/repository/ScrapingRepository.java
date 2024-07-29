package com.bestpie.scraper.api.repository;

import com.bestpie.scraper.common.entity.BestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapingRepository extends JpaRepository<BestPost, Long> {
    boolean existsByTitle(String title);
}
