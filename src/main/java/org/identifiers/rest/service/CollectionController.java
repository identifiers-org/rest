package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.*;
import org.identifiers.jpa.service.*;
import org.identifiers.rest.domain.CollectionSummary;
import org.identifiers.rest.domain.ResourceSummery;
import org.identifiers.rest.domain.UpdateSummary;
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
    SynonymService synonymService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    StatisticsService statisticsService;

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
    * Returns a dump of the regiry that is imported to AWS
    */
    @RequestMapping(value="/expand",method= RequestMethod.GET)
    public @ResponseBody List<CollectionSummary> getExpandedCollections() {
        ArrayList<CollectionSummary> collectionSummaries = new ArrayList<>();
        Set<Collection> collections = collectionService.findNonObsolete();
        logger.info("Number of collections found "+collections.size());

        for(Collection collection: collections){
            List<Resource> resources = resourceService.findNonObsoleteResources(collection);
            logger.info("Number of resources found "+resources.size());


            List<ResourceSummery> resourceSummeries = new ArrayList<>();
            for (Resource resource: resources) {
                ResourceSummery resourceSummery = new ResourceSummery(resource);
                resourceSummery.setTestString(statisticsService.findKeyword(resource));

                String localId=resource.getExample();
                String accessURL=resource.getUrlPrefix();
                if(collection.getPrefixedId()==1){
                    int colonPos = localId.indexOf(":");
                    accessURL = accessURL + localId.substring(0,colonPos+1)+"{$id}"+resource.getUrlSuffix();
                    resourceSummery.setAccessURL(accessURL);

                    localId = localId.substring(colonPos+1);
                    resourceSummery.setLocalId(localId);
                }

                resourceSummeries.add(resourceSummery);
            }

            CollectionSummary collectionSummary = new CollectionSummary(collection);
            collectionSummary.setPrefix(prefixService.findPrefixString(collection));
            collectionSummary.setUrl(prefixService.findIdentifiersUrl(collectionSummary.getPrefix(),configProperties));
            collectionSummary.setResources(resourceSummeries);
            setSynonyms(collection,collectionSummary);
            collectionSummaries.add(collectionSummary);
        }
        return collectionSummaries;
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
        setSynonyms(collection,collectionSummary);
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
    * Returns a list of provider codes for a matching collection using the namespace prefix.
    */
    @RequestMapping(value="/provider/{nsprefix}",method= RequestMethod.GET)
    public @ResponseBody List<ResourceSummery> getProviderCodes(@PathVariable String nsprefix) {
        Prefix prefix = prefixService.findPrefix(nsprefix);
        if(prefix==null){
            throw new IllegalArgumentException("Prefix does not exist: " + nsprefix);
        }
        logger.info("Prefix found by name " + nsprefix);

        List<Resource> resources = resourceService.findNonObsoleteResources(prefix.getCollection());

        if(resources.isEmpty()){
            throw new IllegalArgumentException("Resources not found for prefix: " + nsprefix);
        }
        logger.info("Number of resources found "+resources.size() + " for prefix " + nsprefix);

        List<ResourceSummery> resourceSummeries = new ArrayList<>();
        for (Resource resource: resources) {
            if(!resource.getResourcePrefix().isEmpty()) {
                ResourceSummery resourceSummery = new ResourceSummery(resource);
                resourceSummeries.add(resourceSummery);
            }
        }

        return resourceSummeries;
    }

    @RequestMapping(value="/summary",method= RequestMethod.GET)
    public @ResponseBody UpdateSummary getUpdateSummary() {
        UpdateSummary updateSummary = new UpdateSummary();
        updateSummary.setCollections(collectionService.countByNonObsolete());
        updateSummary.setLastModifiedDate(collectionService.findLastModifiedDate().getModified());
        updateSummary.setResources(resourceService.countByNonObsolete());
        logger.info("Update summary created");

        return updateSummary;
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
            setSynonyms(collection,collectionSummary);
            collectionSummeries.add(collectionSummary);
        }
        return collectionSummeries;
    }

    private void setSynonyms(Collection collection, CollectionSummary collectionSummary){
        List<String> synonyms = new ArrayList<>();
        for(Synonym synonym : synonymService.findSynonyms(collection)){
            synonyms.add(synonym.getName());
        }
        if(!synonyms.isEmpty())
            collectionSummary.setSynonyms(synonyms);
    }


}
