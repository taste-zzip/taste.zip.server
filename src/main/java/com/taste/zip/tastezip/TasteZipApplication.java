package com.taste.zip.tastezip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TasteZipApplication {
	public static void main(String[] args) {
		SpringApplication.run(TasteZipApplication.class, args);
	}

}
