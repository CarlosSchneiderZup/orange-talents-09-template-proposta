package br.com.propostas.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(
                authorizerequests -> authorizerequests
                        .antMatchers(HttpMethod.GET, "/propostas/**").hasAuthority("SCOPE_escopo-proposta")
                        .antMatchers(HttpMethod.POST, "/propostas").hasAuthority("SCOPE_escopo-proposta")
                        .antMatchers(HttpMethod.POST, "/biometrias").hasAuthority("SCOPE_escopo-proposta")
                        .antMatchers(HttpMethod.POST, "/cartoes/**").hasAuthority("SCOPE_escopo-proposta")
                        .antMatchers(HttpMethod.GET, "/actuator/**").hasAuthority("SCOPE_escopo-proposta")
                        .anyRequest().authenticated()
        ).oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }
}
