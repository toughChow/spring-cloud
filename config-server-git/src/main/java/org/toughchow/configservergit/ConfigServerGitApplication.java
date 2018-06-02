package org.toughchow.configservergit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerGitApplication {

	public static void main(String[] args) {
//		SpringApplication.run(ConfigServerGitApplication.class, args);
		new SpringApplicationBuilder(ConfigServerGitApplication.class).web(true).run(args);
	}
}
