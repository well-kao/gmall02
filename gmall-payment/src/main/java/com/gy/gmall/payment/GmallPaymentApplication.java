package com.gy.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@ComponentScan(basePackages = "com.gy.gmall")
@MapperScan(basePackages = "com.gy.gmall.payment.mapper")
@SpringBootApplication
public class GmallPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPaymentApplication.class, args);
	}
}
