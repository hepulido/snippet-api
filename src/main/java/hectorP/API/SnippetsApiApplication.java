package hectorP.API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnippetsApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(SnippetsApiApplication.class, args);
		System.out.println("hello word!");
	}

}
