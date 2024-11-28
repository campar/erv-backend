package com.example.erv.dto.projection;

import java.time.Instant;

import com.example.erv.model.enums.CardType;


public interface CardMinimal {
	Long getId();
	String getRfid();
	Boolean getActive();
	Instant getCreationDate();
	Instant getExpirationDate();
	CardType getCardType();
}
