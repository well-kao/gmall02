package com.gy.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gy.gmall")
@MapperScan(basePackages = "com.gy.gmall.order.mapper")
public class GmallCartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallCartServiceApplication.class, args);
	}
}
