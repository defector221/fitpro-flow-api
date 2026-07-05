package com.fitpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.fitpro.config.CorsProperties;
import com.fitpro.config.JwtProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class FitProFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitProFlowApplication.class, args);
    }
}
