package com.gcu.config;

import java.util.Map;
/**
 * class that contains routes and how they should be secured
 */
public class Routes {

    public static final Map<String, String[]> securedRoutes = Map.of(
        "USER", new String[]{
            "/accounts/options",
            "/accounts/updateUser",
            "/accounts/delete",
            "/accounts/follow",
            "/accounts/unfollow",
            "/posts/edit/**",
            "/posts/delete/**",
            "/posts/create",
            "/replies/create/**",
            "/replies/edit/**",
            "/replies/delete/**", "/api/**", "/unlike/**", "/like/**",
        }
    );
    public static final String[] publicRoutes = new String[]{
        "/accounts/login", "/accounts/register", "/accounts/auth", "/accounts/view/**",
        "/posts/view/**", "/", "/search" /* do we want to allow anyone to search? */,
        "/following", "/css/**", "/images/**", "/js/**", "/resources/**", "/api/", "/apiSummary/", "/swagger.html", "/swagger-ui/**"

    };

    
}
