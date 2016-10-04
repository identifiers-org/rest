package org.identifiers.rest.service;

import org.identifiers.jpa.ConfigProperties;
import org.identifiers.jpa.domain.Collection;
import org.identifiers.jpa.service.CollectionService;
import org.identifiers.jpa.service.PrefixService;
import org.identifiers.rest.domain.IdentifierSummary;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sarala on 04/10/2016.
 */
@Controller
@RequestMapping("/identifiers")
@Configuration
@ComponentScan("org.identifiers.jpa")
@EnableAutoConfiguration
@EnableJpaRepositories("org.identifiers.jpa")
public class IdentifierController {

    @Autowired
    PrefixService prefixService;

    @Autowired
    ConfigProperties configProperties;

    @Autowired
    CollectionService collectionService;

    @RequestMapping(value="/{id}",method= RequestMethod.GET)
    public @ResponseBody
    List<IdentifierSummary> getValidIdentifiers(@PathVariable String id) {

        List<Collection> collections = collectionService.findNonObsolete();
        List<IdentifierSummary> identifierSummaries = new ArrayList<>();
        for (Collection collection:collections){
            if (checkRegexp(id, collection.getPattern())) {
                IdentifierSummary identifierSummary = new IdentifierSummary();
                identifierSummary.setPrefix(prefixService.findPrefixString(collection));
                identifierSummary.setIdentifier(id);
                identifierSummary.setUrl(configProperties.getHttp()+id);
                identifierSummaries.add(identifierSummary);

            }
        }
        return identifierSummaries;
    }

    // TODO: 21/09/2016  - change hardcoded uris
    @RequestMapping(value="/validate/{id}",method= RequestMethod.GET)
    public @ResponseBody IdentifierSummary getValidCollection(@PathVariable String id) {

        String prefix = id.substring(0,id.indexOf(":"));
        String entity = id.substring(id.indexOf(":")+1);

        Collection collection = prefixService.findPrefix(prefix).getCollection();

        if (collection.getPrefixedId()==1) {
            entity = id;
        }
        if (checkRegexp(entity, collection.getPattern())) {
            if(pingURL("http://dev.identifiers.org/"+id)){
                IdentifierSummary identifierSummary = new IdentifierSummary();
                identifierSummary.setPrefix(prefix);
                identifierSummary.setIdentifier(entity);
                identifierSummary.setUrl(configProperties.getHttp()+id);
                return identifierSummary;
            }
        }

        return null;
    }

    private Boolean checkRegexp(String element, String pattern){
        if ((null != element) && (! element.isEmpty()) && (null != pattern) && (! pattern.isEmpty())){
            Pattern pat = Pattern.compile(pattern);
            Matcher matcher = pat.matcher(element);
            return matcher.matches();
        }
        else{
            return false;
        }
    }

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
