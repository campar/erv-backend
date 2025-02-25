package com.example.erv.service.card;

import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.model.enums.CardType;
import com.example.erv.repository.card.CardRepository;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.user.UserAttribute;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.SecurityUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CardService extends CardServiceBasic {
    private static final Logger logger = LogManager.getLogger(CardService.class);

    @Autowired
    @Getter
    private SecurityUtils securityUtils;
    @Autowired
    @Getter
    private DepartmentUnitService departmentUnitService;


    public CardService(CardRepository repository) {
        super(repository);
    }

    @Override
    public Card findByRfid(String rfid) {
        if (!StringUtils.isBlank(rfid)) {
            return super.findByRfid(rfid.toLowerCase());
        }
        throw new NoSuchElementException(rfid);
    }

    @Override
    public Card insert(Card object) throws ErvException {
        if (!StringUtils.isBlank(object.getRfid())) {
            object.setRfid(object.getRfid().toLowerCase());
        }
        return super.insert(object);
    }

    @Override
    public Card update(Card object) throws ErvException {
        if (!StringUtils.isBlank(object.getRfid())) {
            object.setRfid(object.getRfid().toLowerCase());
        }
        return super.update(object);
    }

    @Override
    public Page<Card> findAll(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAll(pageable);
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<Card> spec = SpecificationBasic.<Card>get()
                .joinAnd(CardAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).or(CardAttribute.USER, null);
        return super.findAll(spec.getSpecification(), pageable);
    }

    @Override
    public Page<Card> findAll(Specification<Card> specification, Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
            return super.findAll(specification, pageable);
        }
        List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
        SpecificationBasic<Card> spec = SpecificationBasic.<Card>get()
                .leftJoinAnd(CardAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).or(CardAttribute.USER, null)
                .and(specification);
        return super.findAll(spec.getSpecification(), pageable);
    }

    public void validateCard(Card card) throws InvalidCardException {
        if (Boolean.FALSE.equals(card.getActive())) {
            throw new InvalidCardException(card);
        }
    }

    public void createCardForUser(User user, String rfid) {
        try {
            Card card = new Card();
            card.setRfid(rfid);
            card.setUser(user);
            card.setActive(true);
            card.setExpirationDate(ZonedDateTime.now().plusYears(5).toInstant());
            card.setCardType(CardType.PERSONAL);
            insert(card);
        } catch (ErvException e) {
            logger.error("Couldn't create card [{}]", rfid, e);
        }
    }
}
