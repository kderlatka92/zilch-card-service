package kderlatka.cardservice.service;

import kderlatka.cardservice.domain.Card;
import kderlatka.cardservice.dto.CardCreateRequest;
import kderlatka.cardservice.provider.ExternalCardProvider;
import kderlatka.cardservice.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final List<ExternalCardProvider> providers;

    public Card getCard(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NoSuchElementException("card with number " + cardNumber + " not found."));
    }

    public Card createCard(CardCreateRequest request) {
        Card card = findProvider(request.getCardScheme())
                .generateCard(request)
                .asCreatedCard(request);

        Card savedCard = cardRepository.save(card);
        log.info("Card created successfully: {}", savedCard);
        return savedCard;
    }

    private ExternalCardProvider findProvider(CardCreateRequest.CardScheme scheme) {
        return providers.stream()
                .filter(provider -> provider.supports(scheme))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No provider found for scheme: " + scheme)
                );
    }
}
