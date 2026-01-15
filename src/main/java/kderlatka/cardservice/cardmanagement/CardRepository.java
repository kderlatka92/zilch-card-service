package kderlatka.cardservice.cardmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    default Card findByIdOrThrow(UUID cardId) {
        return findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    Optional<Card> findByCardNumber(String cardNumber);

    default Card findByCardNumberOrThrow(String cardNumber) {
        return findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found for card number: " + cardNumber));
    }


}