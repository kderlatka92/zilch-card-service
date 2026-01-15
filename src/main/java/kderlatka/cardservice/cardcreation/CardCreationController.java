package kderlatka.cardservice.cardcreation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card")
@AllArgsConstructor
@Slf4j
@Tag(name = "Card Creation", description = "Endpoints for card creation")
public class CardCreationController {

    private final CardCreationService cardCreationService;

    @PostMapping
    @Operation(summary = "Create new card prospect", description = "Create a new card prospect with provided details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Card creation requested",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardProspect.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body"
            )
    })
    public ResponseEntity<CardProspect> createCard(@RequestBody CardCreateRequest request) {
        CardProspect card = cardCreationService.createCardProspect(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(card);
    }
}
