package com.pbl.kelembapantanah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class KelembapanTanahServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KelembapanTanahServiceApplication.class, args);
	}

}
