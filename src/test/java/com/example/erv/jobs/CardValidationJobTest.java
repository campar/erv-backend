package com.example.erv.jobs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import com.example.erv.config.AppConfig;
import com.example.erv.jobs.cardvalidation.CardValidationJob;
import com.example.erv.model.Card;
import com.example.erv.service.card.CardService;

@SpringBootTest
class CardValidationJobTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardValidationJob cardValidationJob;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testExecute() {
        // Given
        List<Card> cards = new ArrayList<>();
        Card card1 = new Card();
        card1.setActive(true);
        card1.setExpirationDate(ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())).minusDays(1).toInstant()); // Expired card
        cards.add(card1);

        Card card2 = new Card();
        card2.setActive(true);
        card2.setExpirationDate(ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())).minusDays(5).toInstant()); // Another expired card
        cards.add(card2);

        // Mocking cardService to return the expired cards
        when(cardService.findAll(any(Specification.class))).thenReturn(cards);

        // When
        cardValidationJob.execute(jobExecutionContext);

        // Then
        // Capture the saved cards and verify they were marked as inactive
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardService, times(2)).save(cardCaptor.capture());

        // Verify that both cards are set to inactive
        List<Card> savedCards = cardCaptor.getAllValues();
        for (Card savedCard : savedCards) {
            assert !savedCard.getActive(); // Assert the card is inactive
        }

        // Also verify that findAll was called once
        verify(cardService, times(1)).findAll(any(Specification.class));
    }
}
