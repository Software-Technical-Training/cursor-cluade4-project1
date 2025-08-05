package com.groceryautomation.graphql.resolver.query;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Main Query resolver that delegates to specific query resolvers.
 * Spring GraphQL will automatically pick up @QueryMapping methods from all @Controller classes.
 */
@Controller
public class QueryResolver {
    // This class serves as a marker for the Query type
    // Individual query methods are implemented in domain-specific resolvers
}