package com.example.pioneerpixel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PioneerPixelApplication {

	public static void main(String[] args) {
		SpringApplication.run(PioneerPixelApplication.class, args);
	}

}
