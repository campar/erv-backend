package com.example.erv.jobs.cardvalidation;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.erv.model.Card;
import com.example.erv.service.card.CardAttribute;
import com.example.erv.service.card.CardService;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;

@Component
public class CardValidationJob implements Job {
    private static final Logger logger = LogManager.getLogger(CardValidationJob.class);

    @Autowired
    private CardService cardService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("Card validation job started.");
        List<Card> cards = cardService.findAll(SpecificationBasic.<Card>where(CardAttribute.EXPIRATION_DATE, SearchOperator.LTE, ZonedDateTime.now()).getSpecification());
        for (Card card : cards) {
            card.setActive(false);
            cardService.save(card);
        }
        logger.info("Card validation job ended.");
    }
}
