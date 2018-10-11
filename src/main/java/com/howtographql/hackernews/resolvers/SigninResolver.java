package com.howtographql.hackernews.resolvers;

import com.howtographql.hackernews.models.*;
import com.coxautodev.graphql.tools.GraphQLResolver;

public class SigninResolver implements GraphQLResolver<SigninPayload> {

  public User user(SigninPayload payload) {
      return payload.getUser();
  }
}