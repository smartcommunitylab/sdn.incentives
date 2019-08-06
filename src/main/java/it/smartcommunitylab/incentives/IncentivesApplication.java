package it.smartcommunitylab.incentives;

import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import it.smartcommunitylab.incentives.model.IncentiveModel;

@SpringBootApplication
public class IncentivesApplication {

	public static void main(String[] args) {
		SpringApplication.run(IncentivesApplication.class, args);
	}

	@Bean
	public IncentiveModel getIncentiveModel() {
		Yaml yaml = new Yaml(new Constructor(IncentiveModel.class));
		InputStream inputStream = this.getClass()
		 .getClassLoader()
		 .getResourceAsStream("incentives.yml");
		IncentiveModel model = yaml.load(inputStream);
		return model;

	}
}
