package com.lhind.annualleavemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.enums.Role;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers("/login", "/")
            .permitAll()
            .antMatchers("/admin/**")
            .hasAuthority(String.valueOf(Role.ADMIN))
            .antMatchers("/manager/**")
            .hasAuthority(String.valueOf(Role.MANAGER))
            .antMatchers("/user/**")
            .hasAuthority(String.valueOf(Role.EMPLOYEE))
            .anyRequest()
            .authenticated()
            .and()
            .authenticationProvider(authenticationProvider())
            .formLogin()
            .loginPage("/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .defaultSuccessUrl("/mainApp")
            .permitAll()
            .and()
            .logout()
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedPage("/access-denied");

        httpSecurity.headers().frameOptions().sameOrigin();

        return httpSecurity.build();
    }
}
