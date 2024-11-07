package co.abcbank.service.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecord {
    private String date;
    private Long productId;
    private String currency;
    private Double price;
    private String productName;
}
