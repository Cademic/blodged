package com.gcu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * class that contains our password encoder as a bean so that it can be autowired
 */
@Configuration
public class EncodingConfig {

    private PasswordEncoder encoder = new BCryptPasswordEncoder();
    /**
     * bean of our password encoder so that it can be autowired
     * @return returns an instance of PasswordEncoder
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return encoder;
    }

}