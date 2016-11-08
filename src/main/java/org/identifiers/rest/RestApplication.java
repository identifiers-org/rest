package org.identifiers.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Created by sarala on 29/09/2016.
 */

@SpringBootApplication
@EnableJpaRepositories("org.identifiers.jpa")
public class RestApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RestApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RestApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String datacenter = System.getenv("DATACENTRE");
        if(datacenter!=null){
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, datacenter);
        }else{
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "hx");
        }
        logger.info("Rest service running in " + datacenter);
        super.onStartup(servletContext);
    }

}
