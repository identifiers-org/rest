package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.domain.Prefix;
import org.identifiers.jpa.domain.Resource;
import org.identifiers.jpa.service.*;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by sarala on 28/09/2016.
 */

@Controller
@RequestMapping("/collections")
@Configuration
@ComponentScan("org.identifiers.jpa")
@EnableAutoConfiguration
@EnableJpaRepositories("org.identifiers.jpa")
public class CollectionController {

    @Autowired
    PrefixService prefixService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ConfigProperties configProperties;


    /*
    * Returns all nonobsolete collections ie.(obsolete flag set to '0')
    */
    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody List<CollectionSummary> getCollections() {
        List<Collection> collections = collectionService.findNonObsolete();
        return setPrefixes(collections);
    }

    /*
    * Returns the collection details and the related nonobsolete resources
    */
    @RequestMapping(value="/{collectionId}",method= RequestMethod.GET)
    public @ResponseBody CollectionSummary getCollection(@PathVariable String collectionId) {
        Collection collection = collectionService.findById(collectionId);
        List<Resource> resources = resourceService.findNonObsoleteResources(collection);

        List<ResourceSummery> resourceSummeries = new ArrayList<>();
        for (Resource resource: resources) {
            ResourceSummery resourceSummery = new ResourceSummery(resource);
            resourceSummeries.add(resourceSummery);
        }

        CollectionSummary collectionSummary = new CollectionSummary(collection);
        collectionSummary.setPrefix(prefixService.findPrefixString(collection));
        collectionSummary.setUrl(prefixService.findIdentifiersUrl(collectionSummary.getPrefix(),configProperties));
        collectionSummary.setResources(resourceSummeries);
        return collectionSummary;
    }

    /*
    * Returns a list of matching collections using the name field.
    */
    @RequestMapping(value="/name/{name}",method= RequestMethod.GET)
    public @ResponseBody List<CollectionSummary> getCollectionsSimilarTo(@PathVariable String name) {
        List<Collection> collections = collectionService.findCollections(name);
        return setPrefixes(collections);
    }

    /*
    * Populates prefixes for a given list of collections
    */
    private List<CollectionSummary> setPrefixes(List<Collection> collections){
        List<CollectionSummary> collectionSummeries = new ArrayList<>();
        for (Collection collection: collections) {
            CollectionSummary collectionSummary = new CollectionSummary(collection);
            collectionSummary.setPrefix(prefixService.findPrefixString(collection));
            collectionSummary.setUrl(prefixService.findIdentifiersUrl(collectionSummary.getPrefix(),configProperties));
            collectionSummeries.add(collectionSummary);
        }
        return collectionSummeries;

    }





}
