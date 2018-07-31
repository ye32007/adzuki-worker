package com.adzuki.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "com.adzuki" })
@MapperScan(basePackages = { "com.adzuki.worker.mapper" })
@ImportResource(locations={"classpath:applicationContext.xml"})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
