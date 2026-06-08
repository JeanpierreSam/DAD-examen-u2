package pe.edu.upeu.ms_gestion_alumno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsGestionAlumnoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionAlumnoApplication.class, args);
	}

}
