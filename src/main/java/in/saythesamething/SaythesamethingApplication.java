package in.saythesamething;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class SaythesamethingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaythesamethingApplication.class, args);
	}

}
