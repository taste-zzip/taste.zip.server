package com.taste.zip.tastezip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // CI 과정에서 임시적으로 exclude, DB 설정 후 지우기

@SpringBootApplication
public class TasteZipApplication {
	public static void main(String[] args) {
		SpringApplication.run(TasteZipApplication.class, args);
	}

}
