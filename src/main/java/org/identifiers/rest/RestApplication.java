package org.identifiers.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by sarala on 29/09/2016.
 */

@SpringBootApplication
@EnableJpaRepositories("org.identifiers.jpa")
public class RestApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RestApplication.class);
    }

    public static void main(String[] args) {
        String datacenter = System.getenv("DATACENTRE");
        if(datacenter!=null){
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, datacenter);
        }
        SpringApplication.run(RestApplication.class, args);
    }

}
