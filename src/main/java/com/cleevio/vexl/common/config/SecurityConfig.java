package com.cleevio.vexl.common.config;

import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterAfter(new SecurityFilter(signatureService, userService), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api-docs/**").permitAll()
				.antMatchers("/actuator/health").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/confirmation/phone").anonymous()
                .antMatchers(HttpMethod.POST, "/api/v1/user/confirmation/code").anonymous()
                .antMatchers(HttpMethod.POST, "/api/v1/user/confirmation/challenge").anonymous()
                .anyRequest().authenticated();
    }
}
