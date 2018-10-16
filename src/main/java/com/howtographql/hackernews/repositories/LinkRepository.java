package com.howtographql.hackernews.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.howtographql.hackernews.models.Feed;
import com.howtographql.hackernews.models.Link;
import com.howtographql.hackernews.models.LinkFilter;
import com.howtographql.hackernews.models.Vote;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

public class LinkRepository {
    
    private final MongoCollection<Document> links;
    private final VoteRepository voteRepository;

    public LinkRepository(MongoCollection<Document> links, VoteRepository voteRepository) {
        this.links = links;
        this.voteRepository = voteRepository;
    }

    public Link findById(String id) {
        Document doc = links.find(eq("_id", new ObjectId(id))).first();
        return link(doc);
    }
    
    public List<Link> getAllLinks(LinkFilter filter, int skip, int first) {
        Optional<Bson> mongoFilter = Optional.ofNullable(filter).map(this::buildFilter);
        List<Link> allLinks = new ArrayList<>();
        FindIterable<Document> documents = mongoFilter.map(links::find).orElseGet(links::find);
        for (Document doc : documents.skip(skip).limit(first)) {
            allLinks.add(link(doc));
        }
        return allLinks;
    }

    public Feed getFeed(LinkFilter filter, int skip, int first) {
        return new Feed(getAllLinks(filter, skip, first), links.count());
    }

    private Bson buildFilter(LinkFilter filter) {
        String descriptionPattern = filter.getDescriptionContains();
        String urlPattern = filter.getUrlContains();
        Bson descriptionCondition = null;
        Bson urlCondition = null;

        if (descriptionPattern != null && !descriptionPattern.isEmpty()) {
            descriptionCondition = regex("description", ".*" + descriptionPattern + ".*", "i");
        }
        if (urlPattern != null && !urlPattern.isEmpty()) {
            urlCondition = regex("url", ".*" + urlPattern + ".*", "i");
        }
        if (descriptionCondition != null && urlCondition != null) {
            return or(descriptionCondition, urlCondition);
        }
        return descriptionCondition != null ? descriptionCondition : urlCondition;  
    }
    
    public Link saveLink(Link link) {
        Document doc = new Document();
        doc.append("url", link.getUrl());
        doc.append("description", link.getDescription());
        doc.append("postedBy", link.getUserId());
        links.insertOne(doc);
        return link(doc);

    }

    // public Link deleteLink(String linkId) {
    //     Link link = findById(linkId);
    //     links.deleteOne(eq("_id", new ObjectId(linkId)));
    //     return link;
    // }
    
    private Link link(Document doc) {
        String linkId = doc.get("_id").toString();
        List<Vote> votes = voteRepository.findByLinkId(linkId);
        return new Link(
                linkId,
                doc.getString("url"),
                doc.getString("description"),
                doc.getString("postedBy"),
                votes
                );
    }
}