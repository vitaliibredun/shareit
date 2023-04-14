package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ShareItServer {

	public static void main(String[] args) {

		SpringApplication.run(ShareItServer.class, args);
	}
}