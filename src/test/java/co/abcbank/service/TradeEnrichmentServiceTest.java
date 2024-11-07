package co.abcbank.service;

import co.abcbank.service.web.service.ProductRepository;
import co.abcbank.service.web.service.TradeEnrichmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeEnrichmentServiceTest {

    private static final String INPUT_HEADER = "date,productId,currency,price\n";
    private static final String OUTPUT_HEADER = "date,productName,currency,price\n";
    private static final String DATE_VALID = "20160101";
    private static final String DATE_INVALID = "invalid_date";
    private static final long EXISTING_PRODUCT_ID = 1L;
    private static final long MISSING_PRODUCT_ID = 999L;
    private static final String EXISTING_PRODUCT_NAME = "Treasury Bills Domestic";
    private static final String MISSING_PRODUCT_NAME = "Missing Product Name";
    private static final String CURRENCY = "EUR";
    private static final double PRICE = 10.0;

    @MockBean
    private static ProductRepository productRepository;

    @Autowired
    private TradeEnrichmentService tradeEnrichmentService;

    @BeforeEach
    public void init() {
        when(productRepository.getProductName(EXISTING_PRODUCT_ID)).thenReturn(EXISTING_PRODUCT_NAME);
        when(productRepository.getProductName(MISSING_PRODUCT_ID)).thenReturn(MISSING_PRODUCT_NAME);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testEnrichTrades(MockMultipartFile file, String expectedOutput) {
        CompletableFuture<String> futureResult = tradeEnrichmentService.enrichTrades(file);
        String result = futureResult.join();

        assertEquals(expectedOutput, result);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        createFileWithContent(fillFileContent(DATE_VALID,EXISTING_PRODUCT_ID)),
                        OUTPUT_HEADER + DATE_VALID + "," + EXISTING_PRODUCT_NAME + "," + CURRENCY + "," + String.format("%.2f", PRICE) + "\n"
                ),
                Arguments.of(
                        createFileWithContent(fillFileContent(DATE_INVALID,EXISTING_PRODUCT_ID)),
                        OUTPUT_HEADER
                ),
                Arguments.of(
                        createFileWithContent(fillFileContent(DATE_VALID,MISSING_PRODUCT_ID)),
                        OUTPUT_HEADER + DATE_VALID + "," + MISSING_PRODUCT_NAME + "," + CURRENCY + "," + String.format("%.2f", PRICE) + "\n"
                ),
                Arguments.of(
                        createFileWithContent(INPUT_HEADER),
                        OUTPUT_HEADER
                ),
                Arguments.of(
                        createFileWithContent(fillFileContent(DATE_VALID,EXISTING_PRODUCT_ID)
                                + DATE_VALID + "," + EXISTING_PRODUCT_ID + "," + CURRENCY + "\n"),
                        OUTPUT_HEADER + DATE_VALID + "," + EXISTING_PRODUCT_NAME + "," + CURRENCY + "," + String.format("%.2f", PRICE) + "\n"
                ),
                Arguments.of(
                        createLargeDatasetFile(),
                        generateLargeDatasetExpectedOutput()
                )
        );
    }
    private static String fillFileContent(String date, Long productId){
        return INPUT_HEADER + date + "," + productId + "," + CURRENCY + "," + PRICE + "\n";
    }
    private static MockMultipartFile createFileWithContent(String content) {
        return new MockMultipartFile("file", "trade.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
    }

    private static MockMultipartFile createLargeDatasetFile() {
        StringBuilder csvContentBuilder = new StringBuilder(INPUT_HEADER);
        for (int i = 0; i < 1000; i++) {
            csvContentBuilder.append(DATE_VALID).append(",").append(EXISTING_PRODUCT_ID).append(",").append(CURRENCY).append(",").append(PRICE).append("\n");
        }
        return createFileWithContent(csvContentBuilder.toString());
    }

    private static String generateLargeDatasetExpectedOutput() {
        StringBuilder expectedOutput = new StringBuilder(OUTPUT_HEADER);
        for (int i = 0; i < 1000; i++) {
            expectedOutput.append(DATE_VALID).append(",").append(EXISTING_PRODUCT_NAME).append(",").append(CURRENCY).append(",").append(String.format("%.2f", PRICE)).append("\n");
        }
        return expectedOutput.toString();
    }
}
