package com.elasticcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ComponentScan
@EnableScheduling
@SpringBootApplication
public class SpringBootConsoleApplication {

	public static void main(String[] args) throws Exception {

		SpringApplication app = new SpringApplication(SpringBootConsoleApplication.class);
		app.run(args);
	}

}