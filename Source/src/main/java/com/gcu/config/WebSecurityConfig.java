package com.gcu.config;

import com.gcu.controller.util.UserUtil;
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

import com.gcu.controller.util.PostUtil;
import com.gcu.data.service.UserBusinessService;

/**
 * Spring configuration class for Spring Security
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    private @Autowired UserBusinessService service;
    private @Autowired PasswordEncoder passwordEncoder;

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
     * method that contains the security filter chain to manage privileges.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
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
                        .accessDeniedPage("/error"));

        return http.build();
    }
}
