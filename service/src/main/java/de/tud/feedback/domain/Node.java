package de.tud.feedback.domain;

import org.neo4j.ogm.annotation.GraphId;

public class Node {

    @GraphId
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}