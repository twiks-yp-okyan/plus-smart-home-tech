package ru.yandex.practicum.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/inventory/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/orders/**").hasRole("USER")
                        .pathMatchers(HttpMethod.GET, "/api/orders/{id}").hasRole("USER")
                        .pathMatchers(HttpMethod.GET, "/api/orders/by-email").hasRole("USER")
                        .pathMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .pathMatchers("/api/products/**").hasRole("ADMIN")
                        .pathMatchers("/api/categories/**").hasRole("ADMIN")
                        .pathMatchers("/api/inventory/**").hasRole("ADMIN")
                        .pathMatchers("/swagger/**", "/swagger-ui/**", "/openapi/**", "/api-docs/**").permitAll()
                        .anyExchange().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails ivan = User.builder()
                .username("ivan")
                .password(passwordEncoder.encode("ivan"))
                .roles("USER")
                .build();

        UserDetails anna = User.builder()
                .username("anna")
                .password(passwordEncoder.encode("anna"))
                .roles("USER", "ADMIN")
                .build();

        return new MapReactiveUserDetailsService(ivan, anna);
    }
}