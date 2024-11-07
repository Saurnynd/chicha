package co.abcbank.service.web.service;

import co.abcbank.service.web.model.TradeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TradeEnrichmentService {
    private final ProductRepository productRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Async
    public CompletableFuture<String> enrichTrades(MultipartFile file) {
        List<TradeRecord> trades = parseCsv(file);

        List<TradeRecord> enrichedTrades = trades.parallelStream()
                .filter(this::isValidTrade)
                .map(this::enrichTrade)
                .toList();

        return CompletableFuture.completedFuture(convertToCsvOutput(enrichedTrades));
    }

    private List<TradeRecord> parseCsv(MultipartFile file) {
        List<TradeRecord> trades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            reader.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                try {
                    TradeRecord trade = new TradeRecord(parts[0], Long.parseLong(parts[1]), parts[2], Double.parseDouble(parts[3]), null);
                    trades.add(trade);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return trades;
    }

    private String convertToCsvOutput(List<TradeRecord> trades) {
        StringBuilder csvOutput = new StringBuilder("date,productName,currency,price\n");

        trades.forEach(trade -> csvOutput.append(String.format("%s,%s,%s,%.2f\n",
                trade.getDate(), trade.getProductName(), trade.getCurrency(), trade.getPrice())));

        return csvOutput.toString();
    }

    private boolean isValidTrade(TradeRecord trade) {
        try {
            LocalDate.parse(trade.getDate(), dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format for trade: " + trade.getDate());
            return false;
        }
    }

    private TradeRecord enrichTrade(TradeRecord trade) {
        String productName = productRepository.getProductName(trade.getProductId());
        trade.setProductName(productName);
        return trade;
    }
}
