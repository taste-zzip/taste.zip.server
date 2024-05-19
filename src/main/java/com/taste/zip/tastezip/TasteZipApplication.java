package com.taste.zip.tastezip;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.Arrays;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@OpenAPIDefinition(
    servers = {@Server(url = "/")}
)
@SpringBootApplication
public class TasteZipApplication {
	public static void main(String[] args) {
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
      SpringApplication.run(TasteZipApplication.class, args);
	}

}
