package org.identifiers.rest.service;

import org.identifiers.jpa.domain.Resource;
import org.identifiers.jpa.service.ResourceService;
import org.identifiers.rest.domain.ResourceSummery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by sarala on 03/10/2016.
 */
@Controller
@RequestMapping("/resources")
@Configuration
@ComponentScan("org.identifiers.jpa")
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
        if(resource == null){
            throw new IllegalArgumentException("Required {prefix}:{identifier}");
        }

        ResourceSummery resourceSummery = new ResourceSummery(resource);
        return resourceSummery;
    }
}
