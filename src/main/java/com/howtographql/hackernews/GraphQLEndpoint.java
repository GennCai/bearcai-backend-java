package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.SchemaParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.servlet.annotation.WebServlet;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import io.leangen.graphql.GraphQLSchemaGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import graphql.servlet.GraphQLContext;


@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {
    private static final LinkRepository linkRepository;
    private static final UserRepository userRepository;
    private static final VoteRepository voteRepository;

    static {
        //Change to `new MongoClient("mongodb://<host>:<port>/hackernews")`
        //if you don't have Mongo running locally on port 27017
        MongoDatabase mongo = new MongoClient().getDatabase("hackernews");
        linkRepository = new LinkRepository(mongo.getCollection("links"));
        userRepository = new UserRepository(mongo.getCollection("users"));
        voteRepository = new VoteRepository(mongo.getCollection("votes"));
    }
    public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser().file("schema.graphqls") //parse the schema file created earlier
                .resolvers(
                    new Query(linkRepository, userRepository), 
                    new Mutation(linkRepository, userRepository, voteRepository),
                    new SigninResolver(),
                    new LinkResolver(userRepository),
                    new VoteResolver(linkRepository, userRepository)
                )
                .scalars(Scalars.dateTime) //register the new scalar
                .build().makeExecutableSchema();
    }

    /* private static GraphQLSchema buildSchema() {
        Query query = new Query(linkRepository, userRepository); //create or inject the service beans
        LinkResolver linkResolver = new LinkResolver(userRepository);
        Mutation mutation = new Mutation(linkRepository, userRepository, voteRepository);
        
        return new GraphQLSchemaGenerator()
                .withOperationsFromSingletons(query, linkResolver, mutation) //register the beans
                .generate(); //done :)
    } */
    
    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        User user = request
            .map(req -> req.getHeader("Authorization"))
            .filter(id -> !id.isEmpty())
            .map(id -> id.replace("Bearer ", ""))
            .map(userRepository::findById)
            .orElse(null);
        return new AuthContext(user, request, response);
    }

    @Override
    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
        return errors.stream()
                .filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
                .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
                .collect(Collectors.toList());
    }
}