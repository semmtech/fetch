package com.semmtech.laces.fetch.config;

import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.RelaticsService;
import com.semmtech.laces.fetch.security.rest.filters.AddOnRelaticsProjectFilter;
import com.semmtech.laces.fetch.security.rest.filters.LoggingForwardedHeaderFilter;
import com.semmtech.laces.fetch.security.rest.headers.RelaticsAddOnAllowFromStrategy;
import com.semmtech.laces.fetch.security.service.MongoUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MongoUserDetailsService userDetailsService;
    private final AddOnConfigurationService addOnConfigurationService;
    private final RelaticsService relaticsService;

    @Value("${server.basedomain}")
    String baseDomain;

    public SecurityConfig(MongoUserDetailsService userDetailsService, AddOnConfigurationService addOnConfigurationService, RelaticsService relaticsService) {
        this.userDetailsService = userDetailsService;
        this.addOnConfigurationService = addOnConfigurationService;
        this.relaticsService = relaticsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * Define security configuration:
     *  - open for all:
     *    - the add-on application (for now)
     *    - the api for the add-on application
     *    - the healthcheck endpoint used by Kubernetes for monitoring and auto reloading pods.
     *  - all other urls require authetication
     *  - the login page is served from the admin application
     *  - spring exposes /authenticate to receive authentication requests
     *
     * @param http Spring HttpSecurity object to configure
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .cors()
                .configurationSource(corsConfigurationSource())
            .and()
                .headers()
                .frameOptions().disable()
                .addHeaderWriter(headerWriter(allowFromStrategy()))
            .and()
                .authorizeRequests()
                    .antMatchers("/add-on/**","/api/visualization/**", "/actuator/health", "/authentication/**", "/api/import/**")
                        .permitAll()
                    .antMatchers("/**")
                        .authenticated()
            .and()
                .httpBasic()
            .and()
                .formLogin()
                    .loginProcessingUrl("/authenticate")
                    .loginPage("/authentication/index.html")
                    .defaultSuccessUrl("/admin/index.html", true)
                    .permitAll()
            .and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(
                        loginUrlauthenticationEntryPoint(),
                        httpServletRequest -> true)

                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/authentication/index.html?logout")
                    .invalidateHttpSession(true)
            .and()
                .addFilterBefore(new AddOnRelaticsProjectFilter(allowFromStrategy()), HeaderWriterFilter.class)
            .sessionManagement()
                    .disable();
    }

    @Bean
    public AuthenticationEntryPoint loginUrlauthenticationEntryPoint(){
        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/authentication/index.html");
        entryPoint.setForceHttps(true);
        return entryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public XFrameOptionsHeaderWriter headerWriter(RelaticsAddOnAllowFromStrategy allowFromStrategy) {
        return new XFrameOptionsHeaderWriter(allowFromStrategy);
    }

    @Bean
    public RelaticsAddOnAllowFromStrategy allowFromStrategy() {
        return new RelaticsAddOnAllowFromStrategy(addOnConfigurationService, relaticsService);
    }

    @Bean
    public FilterRegistrationBean<AddOnRelaticsProjectFilter> addOnRelaticsProjectFilter(AllowFromStrategy allowFromStrategy){
        FilterRegistrationBean<AddOnRelaticsProjectFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AddOnRelaticsProjectFilter(allowFromStrategy));
        registrationBean.addUrlPatterns("/add-on/**", "*.js");

        return registrationBean;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        log.info("Set allowed origins to " + baseDomain);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new LoggingForwardedHeaderFilter();
    }

}
