package com.gy.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication

@ComponentScan(basePackages = "com.gy.gmall")
public class GmallListWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallListWebApplication.class, args);
	}
}
