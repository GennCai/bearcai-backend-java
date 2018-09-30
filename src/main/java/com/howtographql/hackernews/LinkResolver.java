package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.GraphQLResolver;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

public class LinkResolver implements GraphQLResolver<Link>{
// public class LinkResolver{
    
  private final UserRepository userRepository;

  public LinkResolver(UserRepository userRepository) {
      this.userRepository = userRepository;
  }

//   @GraphQLQuery
//   public User postedBy(@GraphQLContext Link link) {
  public User postedBy(Link link) {
      if (link.getUserId() == null) {
          return null;
      }
      return userRepository.findById(link.getUserId());
  }
}