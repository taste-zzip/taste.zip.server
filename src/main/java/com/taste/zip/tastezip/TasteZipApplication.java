package com.taste.zip.tastezip;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class TasteZipApplication {
	public static void main(String[] args) {
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
      SpringApplication.run(TasteZipApplication.class, args);
	}

}
