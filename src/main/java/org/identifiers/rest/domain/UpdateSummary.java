package org.identifiers.rest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by sarala on 08/11/2017.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpdateSummary {
    private int collections;
    private int resources;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date lastModifiedDate;

    public UpdateSummary(){}

    public int getCollections() {
        return collections;
    }

    public void setCollections(int collections) {
        this.collections = collections;
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
