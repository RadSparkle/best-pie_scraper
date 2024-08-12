package com.bestpie.scraper.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class BestPostProducer {
    @Autowired
    private KafkaTemplate<Long, Long> kafkaTemplate;

    private static final String TOPIC = "index";

    public void sendMessage(Long id) {
        kafkaTemplate.send(TOPIC, id);
    }
}
