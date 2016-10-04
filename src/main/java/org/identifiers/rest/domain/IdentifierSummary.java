package org.identifiers.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by sarala on 04/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentifierSummary {
    private String prefix;
    private String identifier;
    private String url;

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
