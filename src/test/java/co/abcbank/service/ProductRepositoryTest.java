package co.abcbank.service;

import co.abcbank.service.web.service.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductRepositoryTest {
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    public void testGetProductName_ExistingProductId() {
        String productName = productRepository.getProductName(1L);
        assertEquals("Treasury Bills Domestic", productName);
    }

    @Test
    public void testGetProductName_MissingProductId() {
        String productName = productRepository.getProductName(999L);
        assertEquals("Missing Product Name", productName);
    }
}
