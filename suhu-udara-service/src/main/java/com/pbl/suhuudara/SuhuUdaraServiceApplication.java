package com.pbl.suhuudara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SuhuUdaraServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuhuUdaraServiceApplication.class, args);
	}

}
