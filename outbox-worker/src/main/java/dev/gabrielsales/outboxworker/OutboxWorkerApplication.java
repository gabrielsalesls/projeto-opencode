package dev.gabrielsales.outboxworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutboxWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutboxWorkerApplication.class, args);
	}
}
