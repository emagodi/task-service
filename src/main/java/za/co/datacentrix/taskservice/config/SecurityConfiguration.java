package za.co.datacentrix.taskservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
//public class SecurityConfiguration {
//    /* * At the application startup, during configuration, spring security will try to look for a bean of type SecurityFilterChain
//        * this bean is responsible for configuring all the HTTP security of our application
//     */
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final AuthenticationProvider authenticationProvider;
//    private final Http401UnauthorizedEntryPoint unauthorizedEntryPoint;
//    private final CustomAccessDeniedHandler accessDeniedHandler;
//
//    private static final Long MAX_AGE = 3600L;
//    private static final int CORS_FILTER_ORDER = -102;
//
//
//    @Autowired
//    public SecurityConfiguration(Http401UnauthorizedEntryPoint unauthorizedEntryPoint,
//                          JwtAuthenticationFilter jwtAuthenticationFilter,
//                          AuthenticationProvider authenticationProvider,
//                          CustomAccessDeniedHandler accessDeniedHandler) {
//        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.authenticationProvider = authenticationProvider;
//        this.accessDeniedHandler = accessDeniedHandler;
//    }
//
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(
//                "/api/v1/auth/**",
//                "/v2/api-docs",
//                "/v3/api-docs/**",
//                "/swagger-resources/**",
//                "/swagger-ui/**",
//                "/webjars/**",
//                "/ws",
//                "/swagger-ui.html");
//    }
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(unauthorizedEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler))
//                .authorizeHttpRequests(request ->
//                        request
//                                .requestMatchers(
//                                        "/api/v1/auth/**",
//                                        "/v2/api-docs",
//                                        "/v3/api-docs",
//                                        "/v3/api-docs/**",
//                                        "/swagger-resources",
//                                        "/swagger-resources/**",
//                                        "/configuration/ui",
//                                        "/configuration/security",
//                                        "/swagger-ui/**",
//                                        "/webjars/**",
//                                        "/swagger-ui.html",
//                                        "/forgotPassword/**",
//                                        "/register/**",
//                                        "/api/v1/auth/swagger-ui/**",
//                                        "/v3/api-docs/**",
//                                        "/ws/**",
//                                        "/swagger-ui/**",
//                                        "/swagger-ui.html",
//                                        "/swagger-resources/**",
//                                        "/webjars/**"
//
//                                ).permitAll()
//                                .requestMatchers("/swagger-ui/**").permitAll()
//                                .requestMatchers("/v3/api-docs/**").permitAll()
//                                .requestMatchers("/ws/**").permitAll()
//                                .anyRequest().authenticated())
//                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
//                .authenticationProvider(authenticationProvider).addFilterBefore(
//                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public FilterRegistrationBean corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:3000");
//        config.addAllowedOrigin("http://localhost:8080");
//        config.addAllowedOrigin("https://websocketking.com");
//        config.setAllowedHeaders(Arrays.asList(
//                HttpHeaders.AUTHORIZATION,
//                HttpHeaders.CONTENT_TYPE,
//                HttpHeaders.ACCEPT));
//        config.setAllowedMethods(Arrays.asList(
//                HttpMethod.GET.name(),
//                HttpMethod.POST.name(),
//                HttpMethod.PUT.name(),
//                HttpMethod.DELETE.name()));
//        config.setMaxAge(MAX_AGE);
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//
//        // should be set order to -100 because we need to CorsFilter before SpringSecurityFilter
//        bean.setOrder(CORS_FILTER_ORDER);
//        return bean;
//    }
//}




public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final Http401UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfiguration(Http401UnauthorizedEntryPoint unauthorizedEntryPoint,
                                 JwtAuthenticationFilter jwtAuthenticationFilter,
                                 AuthenticationProvider authenticationProvider,
                                 CustomAccessDeniedHandler accessDeniedHandler) {
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/forgotPassword/**",
                                        "/register/**",
                                        "/api/v1/auth/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/ws/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}