package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.service.CollectionService;
import org.identifiers.jpa.service.PrefixService;
import org.identifiers.rest.domain.IdentifierSummary;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by sarala on 04/10/2016.
 */
@Controller
@RequestMapping("/identifiers")
public class IdentifierController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
            logger.debug("Not a prefixed identifier.");
        }

        List<Collection> collections = collectionService.findNonObsolete();

        for (Collection collection:collections){
            if (checkRegexp(id, collection.getPattern())) {
                identifierSummary = new IdentifierSummary();
                identifierSummary.setPrefix(prefixService.findPrefixString(collection));
                identifierSummary.setIdentifier(id);
                identifierSummary.setUrl(configProperties.getHttp()+identifierSummary.getPrefix()+":"+id);
                identifierSummaries.add(identifierSummary);
            }
        }
        return identifierSummaries;
    }

    private IdentifierSummary createIdentifierSummaryFromId(String id){
        if(!id.contains(":")){
            throw new IllegalArgumentException("Required {prefix}:{identifier}");
        }
        String prefix = id.substring(0,id.indexOf(":"));
        String entity = id.substring(id.indexOf(":")+1);

        Collection collection = prefixService.findPrefix(prefix).getCollection();

        if(collection==null){
            throw new IllegalArgumentException("Unknown prefix");
        }

        if (collection.getPrefixedId()==1) {
            entity = id;
        }
        if (checkRegexp(entity, collection.getPattern())) {
            return new IdentifierSummary(prefix,entity,configProperties.getHttp()+id);
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
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(200);
            int code = connection.getResponseCode();
            if(code >=200 && code<400) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
