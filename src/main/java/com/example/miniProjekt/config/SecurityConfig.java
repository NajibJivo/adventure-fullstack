package com.example.miniProjekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // ====== OFFENTLIGE SIDER (ingen login påkrævet) ======
                        .requestMatchers(
                                "/login.html",
                                "/signup.html",
                                "/perform_login",
                                "/api/auth/signup",
                                "/style.css",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/*.js"
                        ).permitAll()

                        // ====== FORSIDE OG AKTIVITETER (offentlige) ======
                        .requestMatchers("/", "/activity.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll()

                        // ====== USER INFO ENDPOINT ======
                        .requestMatchers("/api/auth/current").authenticated()

                        // ====== ADMIN-KUN SIDER OG ENDPOINTS ======
                        .requestMatchers("/admin-products.html").hasAuthority("OWNER")
                        .requestMatchers("/api/products/**").hasAuthority("OWNER")

                        // Admin kan lave CRUD på aktiviteter
                        .requestMatchers(HttpMethod.POST, "/api/activities/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/activities/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/activities/**").hasAuthority("OWNER")

                        // ====== BOOKING SIDER OG ENDPOINTS (både admin og customer) ======
                        .requestMatchers("/booking.html", "/edit-booking.html").authenticated()
                        .requestMatchers("/api/bookings/**").authenticated()

                        // ====== ACTIVITY EDIT SIDE (kun admin) ======
                        .requestMatchers("/edit-activity.html").hasAuthority("OWNER")

                        // ====== ALT ANDET KRÆVER LOGIN ======
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/activity.html", true)
                        .failureUrl("/login.html?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }
}