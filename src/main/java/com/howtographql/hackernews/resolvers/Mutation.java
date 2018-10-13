package com.howtographql.hackernews.resolvers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.howtographql.hackernews.repositories.*;
import com.howtographql.hackernews.AuthContext;
import com.howtographql.hackernews.models.*;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLRootContext;

public class Mutation implements GraphQLRootResolver{
// public class Mutation{
    
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;
  private final VoteRepository voteRepository;

  public Mutation(LinkRepository linkRepository, UserRepository userRepository, VoteRepository voteRepository) {
      this.linkRepository = linkRepository;
      this.userRepository = userRepository;
      this.voteRepository = voteRepository;
  }
  
  // @GraphQLMutation
  // public Link createLink(String url, String description, @GraphQLRootContext AuthContext context) {
  public Link createLink(String url, String description, DataFetchingEnvironment env) {
      AuthContext context = env.getContext();
      String userId = context.getUser() != null ? context.getUser().getId() : null;
      Link newLink = new Link(url, description, userId, null);
      return linkRepository.saveLink(newLink);
  }
  
  public SigninPayload createUser(String name, AuthData auth) {
    User newUser = new User(name, auth.getEmail(), auth.getPassword());
    User user  = userRepository.saveUser(newUser);
    return new SigninPayload(user.getId(), user);
  }

  public Vote createVote(String linkId, DataFetchingEnvironment env) {
    AuthContext context = env.getContext();
    String userId = context.getUser() != null ? context.getUser().getId() : null;
    ZonedDateTime now = Instant.now().atZone(ZoneOffset.UTC);
    return voteRepository.saveVote(new Vote(now, userId, linkId));
  }

  public SigninPayload signinUser(AuthData auth) throws IllegalAccessException {
    User user = userRepository.findByEmail(auth.getEmail());
    if (user.getPassword().equals(auth.getPassword())) {
        return new SigninPayload(user.getId(), user);
    }
    throw new GraphQLException("Invalid credentials");
  }
}