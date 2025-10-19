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
                                "/",
                                "/index.html",
                                "/login.html",
                                "/signup.html",
                                "/activity.html",
                                "/perform_login",
                                "/api/auth/signup",
                                "/style.css",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/*.js",
                                "/*.css"
                        ).permitAll()

                        // ====== API ENDPOINTS - Aktiviteter (offentlige) ======
                        .requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll()

                        // ====== USER INFO ENDPOINT ======
                        .requestMatchers("/api/auth/current").authenticated()

                        // ====== PRODUKTER/BUTIK ======
                        .requestMatchers("/admin-products.html").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/product/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/product/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasAuthority("OWNER")

                        // ====== AKTIVITETER - Admin kan redigere ======
                        .requestMatchers(HttpMethod.POST, "/api/activities/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/activities/**").hasAuthority("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/activities/**").hasAuthority("OWNER")

                        // ====== BOOKING SIDER OG ENDPOINTS (både admin og customer) ======
                        // ====== BOOKING SIDER OG ENDPOINTS ======
                        .requestMatchers("/booking.html", "/edit-booking.html").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()  // Alle kan oprette
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/**").hasAuthority("OWNER")  // Kun admin kan redigere
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasAuthority("OWNER")  // Kun admin kan slette

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
