package com.pbl.kelembapanudara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class KelembapanUdaraServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KelembapanUdaraServiceApplication.class, args);
	}

}
