package edu.ucla.mednet.iss.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass=true)

public class ApplicationBootstrap {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }


}
