package com.howtographql.hackernews.models;

import java.util.List;

public class Link {
    
    private final String id; //the new field
    private final String url;
    private final String description;
    private final String userId;
    private final List<Vote> votes;

    public Link(String url, String description, String userId, List<Vote> votes) {
        this(null, url, description, userId, votes);
    }

    public Link(String id, String url, String description, String userId, List<Vote> votes) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.userId = userId;
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public List<Vote> getVotes() {
        return votes;
    }
    
}