package com.gy.gmall.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gy.gmall")
public class GmallPassportWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPassportWebApplication.class, args);
	}
}
