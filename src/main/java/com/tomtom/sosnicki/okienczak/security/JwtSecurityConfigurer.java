package com.tomtom.sosnicki.okienczak.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

import static com.tomtom.sosnicki.okienczak.entity.AuthorityEntity.Name.*;

@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy
public class JwtSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;


    public JwtSecurityConfigurer() {
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public StatusCodeAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new StatusCodeAuthenticationEntryPoint();
    }

    @Bean
    public ReturnJwtAuthenticationSuccessHandler returnJwtAuthenticationSuccessHandler() {
        return new ReturnJwtAuthenticationSuccessHandler();
    }

    @Bean
    public StatusCodeAuthenticationFailureHandler statusCodeAuthenticationFailureHandler() {
        return new StatusCodeAuthenticationFailureHandler();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder)
            throws Exception {
        builder.authenticationProvider(jwtAuthenticationProvider());
        builder.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery(
                        "SELECT username, password, 1 " +
                        "FROM account WHERE username = ?"
                )
                .authoritiesByUsernameQuery(
                        "SELECT acc.username, aut.name " +
                        "FROM account acc " +
                        "INNER JOIN authority aut " +
                        "ON acc.account_id = aut.account_id " +
                        "WHERE acc.username = ?"
                );
        builder.inMemoryAuthentication()
                .withUser("admin").password("password")
                .authorities(ROLE_USER.name(), ROLE_ADMIN.name());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint());
        http.csrf().disable();

        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter("/rest/login");
        restAuthenticationFilter.setAuthenticationManager(authenticationManager());
        restAuthenticationFilter.setAuthenticationSuccessHandler(returnJwtAuthenticationSuccessHandler());
        restAuthenticationFilter.setAuthenticationFailureHandler(statusCodeAuthenticationFailureHandler());
        http.addFilterBefore(restAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter("/rest/**");
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager());
        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin().disable();
        http.logout().disable();

        http.authorizeRequests()
                .antMatchers("/rest/login").permitAll()
                .antMatchers("/rest/account").permitAll()
                .antMatchers("/rest/admin/**").hasAuthority(ROLE_ADMIN.name())
                .antMatchers(HttpMethod.GET, "/rest/**").permitAll()
                .antMatchers("/rest/**").hasAuthority(ROLE_USER.name())
                .anyRequest().permitAll();
    }

}
