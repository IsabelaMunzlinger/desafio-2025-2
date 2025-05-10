package br.com.desafiodev.locadora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class LocadoraApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		System.setProperty("apiTokenV4", dotenv.get("apiTokenV4"));
		SpringApplication.run(LocadoraApplication.class, args);
	}

}
