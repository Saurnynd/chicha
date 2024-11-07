package co.abcbank.service.web.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductRepository {
    private final Map<Long, String> productMap = new HashMap<>();

    public ProductRepository() {
        loadProductData();
    }

    private void loadProductData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/product.csv"))) {
            reader.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                Long productId = Long.parseLong(parts[0]);
                String productName = parts[1];
                productMap.put(productId, productName);
            });
        } catch (IOException e) {
            System.err.println("Error loading product data: " + e.getMessage());
        }
    }

    public String getProductName(Long productId) {
        return productMap.getOrDefault(productId, "Missing Product Name");
    }
}
