package com.example.erv.controller.card;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.service.card.CardService;
import com.example.erv.service.permission.Permissions;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class CardControllerBasic extends GenericController<Card> {
    protected final CardService cardService;

    public CardControllerBasic(CardService service) {
        super(service);
        this.cardService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.Card.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Card.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Card.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Card.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.Card.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid Card object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Card.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid Card object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Card.CREATE);
        return super.insert(object);
    }
}
