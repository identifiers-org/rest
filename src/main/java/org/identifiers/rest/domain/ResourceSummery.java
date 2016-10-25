package org.identifiers.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.identifiers.jpa.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sarala on 30/09/2016.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceSummery {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSummery.class);

    private String id;
    private String accessURL;
    private String info;
    private String institution;
    private String location;
    private boolean official;
    private String resourcePrefix;

    public ResourceSummery(Resource resource) {
        logger.info("Creating resouce summary for "+resource.getId());
        id=resource.getId();
        accessURL=resource.getUrlPrefix()+"$id"+resource.getUrlSuffix();
        info=resource.getInfo();
        institution=resource.getInstitution();
        location=resource.getLocation();
        official= resource.getOfficial() == 1;
        resourcePrefix=resource.getResourcePrefix() !=null && !resource.getResourcePrefix().isEmpty() ? resource.getResourcePrefix() : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getResourcePrefix() {
        return resourcePrefix;
    }

    public void setResourcePrefix(String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
    }
}
