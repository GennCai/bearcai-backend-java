package com.howtographql.hackernews;

import java.util.List;
import com.coxautodev.graphql.tools.GraphQLRootResolver;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

public class Query implements GraphQLRootResolver{
// public class Query {
    
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
  
    public Query(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }
  
    // @GraphQLQuery
    // public List<Link> allLinks(LinkFilter filter, @GraphQLArgument(name = "skip", defaultValue = "0") Number skip, @GraphQLArgument(name = "first", defaultValue = "0") Number first) {
    public List<Link> allLinks(LinkFilter filter, Number skip, Number first) {
        return linkRepository.getAllLinks(filter, skip.intValue(), first.intValue());
    }
  
    public List<User> allUsers() {
        return userRepository.getAllUsers();
    }
}