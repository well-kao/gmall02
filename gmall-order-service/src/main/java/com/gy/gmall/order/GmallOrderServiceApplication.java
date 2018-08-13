package com.gy.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.gy.gmall.order.mapper")
@ComponentScan(basePackages = "com.gy.gmall")
@SpringBootApplication
public class GmallOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallOrderServiceApplication.class, args);
	}
}
