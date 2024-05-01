package com.taste.zip.tastezip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // CI 과정에서 임시적 exclude

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TasteZipApplication {
	public static void main(String[] args) {
		SpringApplication.run(TasteZipApplication.class, args);
	}

}
