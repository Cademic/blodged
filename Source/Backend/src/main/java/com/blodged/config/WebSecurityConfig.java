package com.blodged.config;

import com.blodged.controller.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.blodged.controller.util.PostUtil;
import com.blodged.data.service.UserBusinessService;

/**
 * Spring configuration class for Spring Security
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    private @Autowired UserBusinessService service;
    private @Autowired PasswordEncoder passwordEncoder;
    private @Autowired ApiTokenAuthFilter apiTokenAuthFilter;

    /**
     * Manages authentication for users
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public PostUtil postUtil() {
        return new PostUtil();
    }

    /**
     * A bean that retrieves the user util. For use with gravatar functions in views.
     * @return the user utils
     */
    @Bean
    public UserUtil userUtil() {
        return new UserUtil();
    }
    
    /**
     * Bean for session management
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    /**
     * Bean for security context repository
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * method that contains the security filter chain to manage privileges.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository()))
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/accounts/login"))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(Routes.publicRoutes).permitAll()
            .requestMatchers(Routes.securedRoutes.get("USER")).hasAnyAuthority("USER")
            .anyRequest().authenticated()
        )
                .formLogin(form -> form
                        .loginPage("/accounts/login")
                        .usernameParameter("email")
                        .passwordParameter("password").permitAll()
                        .failureHandler(new AuthFailureHandler(service))
                        .defaultSuccessUrl("/", true))
                .logout(logout -> logout
                        .logoutUrl("/accounts/logout")
                        .clearAuthentication(true)
                        .permitAll()
                        .logoutSuccessUrl("/accounts/login").deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .exceptionHandling(handling -> handling
                        .accessDeniedPage("/error"))
                // Add our token authentication filter
                .addFilterBefore(apiTokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
                        
        return http.build();
    }
}
