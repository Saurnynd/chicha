package co.abcbank.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AbcTradeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbcTradeServiceApplication.class, args);
	}

}
