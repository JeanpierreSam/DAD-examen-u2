package pe.edu.upeu.ms_gestion_alumno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class MsGestionAlumnoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionAlumnoApplication.class, args);
	}

}
