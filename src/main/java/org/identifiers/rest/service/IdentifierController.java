package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.domain.Prefix;
import org.identifiers.jpa.service.CollectionService;
import org.identifiers.jpa.service.PrefixService;
import org.identifiers.rest.domain.IdentifierSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by sarala on 04/10/2016.
 */
@Controller
@RequestMapping("/identifiers")
public class IdentifierController {

    private static final Logger logger = LoggerFactory.getLogger(IdentifierController.class);

    @Autowired
    PrefixService prefixService;

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    CollectionService collectionService;

    /*
    * Returns a list of valid prefixes for a given identifier
    */
    @RequestMapping(value="/{id}",method= RequestMethod.GET)
    public @ResponseBody
    List<IdentifierSummary> getValidIdentifiers(@PathVariable String id) {
        List<IdentifierSummary> identifierSummaries = new ArrayList<>();
        IdentifierSummary identifierSummary;
        try {
            identifierSummary = createIdentifierSummaryFromId(id);
            if (identifierSummary != null) {
                identifierSummaries.add(identifierSummary);
                return identifierSummaries;
            }
        }catch (IllegalArgumentException e){
            logger.info("Not a prefixed identifier.");
        }

        Set<Collection> collections = collectionService.findNonObsolete();
        logger.info("Number of collections found "+collections.size());

        for (Collection collection:collections){
            if (checkRegexp(id, collection.getPattern())) {
                logger.info("Pattern matched "+ id + " " + collection.getPattern());
                identifierSummary = new IdentifierSummary();
                identifierSummary.setPrefix(prefixService.findPrefixString(collection));
                identifierSummary.setIdentifier(id);
                identifierSummary.setUrl(configProperties.getHttp()+identifierSummary.getPrefix()+":"+id);
                identifierSummaries.add(identifierSummary);
            }else{
                logger.info("Pattern not matched "+ id + " " + collection.getPattern());
            }
        }
        return identifierSummaries;
    }

    private IdentifierSummary createIdentifierSummaryFromId(String id){
        if(!id.contains(":")){
            throw new IllegalArgumentException("Required {prefix}:{identifier}");
        }
        String prefixString = id.substring(0,id.indexOf(":"));
        String entity = id.substring(id.indexOf(":")+1);

        Prefix prefix = prefixService.findPrefix(prefixString);

        if(prefix==null){
            throw new IllegalArgumentException("Unknown prefix");
        }

        Collection collection = prefix.getCollection();

        logger.info("Collection found "+ collection.getName());

        if (collection.getPrefixedId()==1) {
            //try uppercase identifier as this is commonly used
            entity = prefixString.toUpperCase()+":"+entity;
            if (checkRegexp(entity, collection.getPattern())) {
                return new IdentifierSummary(prefixString,entity,configProperties.getHttp()+entity);
            }else{
                entity = id;
            }
        }
        if (checkRegexp(entity, collection.getPattern())) {
            logger.info("Pattern matched "+ id + " " + collection.getPattern());
            return new IdentifierSummary(prefixString,entity,configProperties.getHttp()+id);
        }else{
            throw new IllegalArgumentException("Invalid identifier pattern");
        }

    }

    /*
    * Validate the given {prefix}:{identifier} scheme.
    * */
    @RequestMapping(value="/validate/{id}",method= RequestMethod.GET)
    public @ResponseBody IdentifierSummary getValidCollection(@PathVariable String id) {

        IdentifierSummary identifierSummary = createIdentifierSummaryFromId(id);

        if (identifierSummary != null && pingURL(identifierSummary.getUrl())) {
            return identifierSummary;
        }
        else {
            throw new IllegalArgumentException("Unable to reach the data record, resource may be down");
        }
    }

    /*
    *checks for regular expression
    */
    private Boolean checkRegexp(String element, String pattern) {
        return element != null && pattern != null && Pattern.matches(pattern, element);
    }

    /*
    * Pings the url to see whether it responds
    */
    private Boolean pingURL(String url_string){
        try {
            URL url = new URL(url_string);
            logger.info("Ping url "+ url_string);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(200);
            int code = connection.getResponseCode();
            if(code >=200 && code<400) {
                logger.info("Ping successful");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Ping successful for "+ url_string);
        return false;
    }
}
