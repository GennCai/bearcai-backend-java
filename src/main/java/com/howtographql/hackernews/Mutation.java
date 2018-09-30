package com.howtographql.hackernews;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

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
      Link newLink = new Link(url, description, context.getUser().getId());
      linkRepository.saveLink(newLink);
      return newLink;
  }
  
  public User createUser(String name, AuthData auth) {
    User newUser = new User(name, auth.getEmail(), auth.getPassword());
    return userRepository.saveUser(newUser);
  }

  public Vote createVote(String linkId, String userId) {
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