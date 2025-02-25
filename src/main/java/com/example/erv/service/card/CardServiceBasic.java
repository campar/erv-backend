package com.example.erv.service.card;

import java.time.ZonedDateTime;
import java.util.List;

import com.example.erv.dto.projection.CardMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.model.enums.CardType;
import com.example.erv.repository.card.CardRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class CardServiceBasic extends GenericService<Card> {
    protected final CardRepository repository;

    public CardServiceBasic(CardRepository repository) {
        super(repository);
        this.repository = repository;
    }
    
    @Override
    public List<CardMinimal> findAllMinimal(){
    	return repository.findAllProjected(CardMinimal.class);
    }

    @Override
    public Card insert(Card object) throws ErvException {
        if (object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
        return repository.save(object);
    }

    @Override
    public Card update(Card object) throws ErvException {
        if (object.getId() == null || repository.findById(object.getId()).isEmpty())
            throw new EntityNotFoundException(String.valueOf(object.getId()));
        return repository.save(object);
    }

    @Override
    public Class<Card> getEntityClass() {
        return Card.class;
    }

    public Card findByRfid(String rfid) {
        return repository.findByRfid(rfid).orElseThrow();
    }

    public List<Card> findByActive(Boolean active) {
        return repository.findByActive(active);
    }

    public List<Card> findByDescription(String description) {
        return repository.findByDescription(description);
    }

    public List<Card> findByCreationDate(ZonedDateTime creationDate) {
        return repository.findByCreationDate(creationDate.toInstant());
    }

    public List<Card> findByExpirationDate(ZonedDateTime expirationDate) {
        return repository.findByExpirationDate(expirationDate.toInstant());
    }

    public List<Card> findByCardType(CardType cardType) {
        return repository.findByCardType(cardType);
    }
}
