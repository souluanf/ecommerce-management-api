package dev.luanfernandes.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "dev.luanfernandes.adapter.out.persistence.repository")
@EntityScan(basePackages = "dev.luanfernandes.adapter.out.persistence.entity")
@SpringBootApplication(scanBasePackages = "dev.luanfernandes")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
