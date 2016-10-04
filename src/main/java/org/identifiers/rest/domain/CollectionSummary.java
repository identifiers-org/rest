package org.identifiers.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.identifiers.jpa.domain.Collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarala on 29/09/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionSummary{

    private String id;
    private String name;
    private String pattern;
    private String definition;
    private String prefix;
    private String url;
    private List<ResourceSummery> resources;


    public CollectionSummary(Collection collection) {
        id = collection.getId();
        name = collection.getName();
        pattern = collection.getPattern();
        definition = collection.getDefinition();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<ResourceSummery> getResources() {
        return resources;
    }

    public void setResources(List<ResourceSummery> resources) {
        this.resources = resources;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
