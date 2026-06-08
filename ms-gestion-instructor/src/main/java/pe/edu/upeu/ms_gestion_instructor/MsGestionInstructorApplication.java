package pe.edu.upeu.ms_gestion_instructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsGestionInstructorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionInstructorApplication.class, args);
	}

}
