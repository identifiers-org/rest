package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.domain.Resource;
import org.identifiers.jpa.service.CollectionService;
import org.identifiers.jpa.service.PrefixService;
import org.identifiers.jpa.service.ResourceService;
import org.identifiers.rest.domain.CollectionSummary;
import org.identifiers.rest.domain.ResourceSummery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by sarala on 28/09/2016.
 */

@Controller
@RequestMapping("/collections")
public class CollectionController {

    private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);

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
        Set<Collection> collections = collectionService.findNonObsolete();
        logger.info("Number of collections found "+collections.size());
        return setPrefixes(collections);
    }

    /*
    * Returns the collection details and the related nonobsolete resources
    */
    @RequestMapping(value="/{collectionId}",method= RequestMethod.GET)
    public @ResponseBody CollectionSummary getCollection(@PathVariable String collectionId) {
        Collection collection = collectionService.findById(collectionId);

        if(collection==null){
            throw new IllegalArgumentException("Invalid collection identifier " + collectionId);
        }
        logger.info("Collection found "+collection.getId());

        List<Resource> resources = resourceService.findNonObsoleteResources(collection);
        logger.info("Number of resources found "+resources.size());


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
        Set<Collection> collections = collectionService.findByNameAndPrefixContaining(name);
        if(collections.isEmpty()){
            throw new IllegalArgumentException("Collection not found: " + name);
        }
        logger.info("Number of collections found "+collections.size() + " by name " + name);

        return setPrefixes(collections);
    }

    /*
    * Populates prefixes for a given list of collections
    */
    private List<CollectionSummary> setPrefixes(Set<Collection> collections){
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
