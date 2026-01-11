package kderlatka.cardservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kderlatka.cardservice.domain.Card;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardNumber(String cardNumber);

}