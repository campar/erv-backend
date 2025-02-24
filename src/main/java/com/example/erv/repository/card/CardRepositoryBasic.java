package com.example.erv.repository.card;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Card;
import com.example.erv.model.enums.CardType;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface CardRepositoryBasic extends GenericRepository<Card> {

	Optional<Card> findByRfid(String rfid);
	List<Card> findByActive(Boolean active);
	List<Card> findByDescription(String description);
	List<Card> findByCreationDate(Instant creationDate);
	List<Card> findByExpirationDate(Instant expirationDate);
	List<Card> findByCardType(CardType cardType);

}
