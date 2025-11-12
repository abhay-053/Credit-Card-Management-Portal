package com.ccms.portal.entity;

import com.ccms.portal.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Builder
@Entity
@Table(name = "credit_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cardNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @Column(nullable = false)
    private Double creditLimit;

    @Column(nullable = false)
    private Double availableLimit;

    @Column(nullable = false)
    private Date expiryDate;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "card_type_id", nullable = false)
    private CardTypeEntity cardType;
}
