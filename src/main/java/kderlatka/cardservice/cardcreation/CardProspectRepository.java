package kderlatka.cardservice.cardcreation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface CardProspectRepository extends JpaRepository<CardProspect, UUID> {

    default CardProspect findByIdOrThrow(UUID prospectId) {
        return findById(prospectId)
                .orElseThrow(() -> new CardProspectNotFoundException("Card prospect not found: " + prospectId));
    }

}