package org.worker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    public UserDetailsServiceFromJson userDetailsServiceFromJson;

    @SuppressWarnings("deprecation")
    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    /**
     * predefined node credentials
     */
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails nodeCredentials = User.builder()
                .username("worker")
                .password("{noop}123")
                .roles("NODE")
                .build();

        return new InMemoryUserDetailsManager(nodeCredentials);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                        {
                            configurer
                                    .requestMatchers("/login", "/static/**").permitAll()
                                    .requestMatchers("/admin/**").hasRole("NODE")
                                    .anyRequest().authenticated();


                        }

                ).formLogin(Customizer.withDefaults())
                .authenticationManager(authman(http, passwordEncoder()))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(inMemoryUserDetailsManager());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authman(HttpSecurity httpSecurity,
                                         NoOpPasswordEncoder passwordEncoder)
            throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(inMemoryUserDetailsManager())
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
                                                       NoOpPasswordEncoder passwordEncoder)
            throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(inMemoryUserDetailsManager())
                .and()
                .userDetailsService(userDetailsServiceFromJson)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();

    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsServiceFromJson);
//    }
}
