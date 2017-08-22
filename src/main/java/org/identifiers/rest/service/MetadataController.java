package org.identifiers.rest.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonLdUrl;
import com.github.jsonldjava.utils.JsonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sarala on 21/08/2017.
 */
@Controller
@RequestMapping("/metadata")
public class MetadataController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Object getMetadata(@PathVariable String id) {
        Object data = null;
        try {
            Document doc = Jsoup.connect("http://identifiers.org/"+id).get();
            Elements scripts = doc.select("script");
            for (Element element : scripts ){
                if(element.attr("type").equals("application/ld+json")) {
                    Object jsonObject = JsonUtils.fromString(element.data());
                    String context = ((Map)jsonObject).get("@context").toString();
                    if(context!=null && !context.isEmpty() && context.contains("http://schema.org")){
                        data = jsonObject; //element.data();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data == null){
            throw new IllegalArgumentException("Metadata is unavailable");
        }else
            return data;

    }
}
