package io.wedeploy.wedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class WeddingSpringBootApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WeddingSpringBootApplication.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WeddingSpringBootApplication.class, args);

		Guests guests = new Guests();

		System.out.println(guests.toJSONString());
	}

}