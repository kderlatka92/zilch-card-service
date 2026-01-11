package kderlatka.cardservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kderlatka.cardservice.Card;
import kderlatka.cardservice.CardService;
import kderlatka.cardservice.dto.CardCreateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
@AllArgsConstructor
@Slf4j
@Tag(name = "Card Management", description = "Endpoints for card operations")
public class CardController {

    private final CardService cardService;

    @PostMapping
    @Operation(
            summary = "Create new card",
            description = "Create a new card with provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Card created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Card.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body"
            )
    })
    public ResponseEntity<Card> createCard(@RequestBody CardCreateRequest request) {
        Card card = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @GetMapping("/{cardNumber}")
    @Operation(
            summary = "Get card by number",
            description = "Retrieve card details by card number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Card found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Card.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found"
            )
    })
    public ResponseEntity<Card> getCard(@PathVariable String cardNumber) {
        Card card = cardService.getCard(cardNumber);
        return ResponseEntity.ok(card);
    }
}
