package org.identifiers.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sarala on 04/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IdentifierSummary {

    private static final Logger logger = LoggerFactory.getLogger(IdentifierSummary.class);
    private String prefix;
    private String identifier;
    private String url;

    public IdentifierSummary() {
    }

    public IdentifierSummary(String prefix, String identifier, String url) {
        logger.info("Creating identifier summary for "+prefix + ":"+ identifier);
        this.prefix = prefix;
        this.identifier = identifier;
        this.url = url;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
