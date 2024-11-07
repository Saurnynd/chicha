package co.abcbank.service.web.controller;


import co.abcbank.service.web.service.TradeEnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AbcTradeServiceController {
    private final TradeEnrichmentService enrichmentService;

    @PostMapping("/enrich")
    public CompletableFuture<ResponseEntity<String>> enrichTrades(@RequestParam("file") MultipartFile file) {
        return enrichmentService.enrichTrades(file)
                .thenApply(ResponseEntity::ok);
    }


}


