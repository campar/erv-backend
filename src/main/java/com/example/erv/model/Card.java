package com.example.erv.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.erv.model.enums.CardType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card")
@EntityListeners(AuditingEntityListener.class)
public class Card {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	@Column(nullable = false, unique = true)
	private String rfid;
	@NotNull
	@Column(nullable = false, columnDefinition = "boolean default true")
	private Boolean active;
	@Column
	private String description;
	@Column
	@CreatedDate
	private Instant creationDate;
	@Column
	private Instant expirationDate;
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CardType cardType;
	@ManyToOne
	@JoinColumn(name = "erv_user", referencedColumnName = "id")
	private User user;
}
