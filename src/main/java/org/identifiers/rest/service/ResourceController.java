package org.identifiers.rest.service;

import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.domain.Resource;
import org.identifiers.jpa.service.ResourceService;
import org.identifiers.rest.domain.CollectionSummary;
import org.identifiers.rest.domain.ResourceSummery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarala on 03/10/2016.
 */
@Controller
@RequestMapping("/resources")
@Configuration
@ComponentScan("org.identifiers.jpa")
@EnableAutoConfiguration
@EnableJpaRepositories("org.identifiers.jpa")
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    /*
    * Returns the resource information for a given id
    */
    @RequestMapping(value="/{resourceId}",method= RequestMethod.GET)
    public @ResponseBody
    ResourceSummery getResource(@PathVariable String resourceId) {
        Resource resource = resourceService.findNonObsoleteResource(resourceId);

        ResourceSummery resourceSummery = new ResourceSummery(resource);

        return resourceSummery;
    }
}
