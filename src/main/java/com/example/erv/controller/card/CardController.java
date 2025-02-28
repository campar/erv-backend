package com.example.erv.controller.card;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.service.card.CardService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/card")
@SecurityRequirement(name = "bearerAuth")
public class CardController extends CardControllerBasic {

	public CardController(CardService service) {
		super(service);
	}

}
