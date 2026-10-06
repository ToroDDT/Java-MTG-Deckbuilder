package com.example.mtg_deckbuilder.config;

import com.example.mtg_deckbuilder.security.DemoAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DemoAuthenticationFilter demoAuthenticationFilter
    ) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/index", "/", "/register", "/login", "/css/**", "/js/**",  "/swagger-ui/**",
                        "/v3/api-docs/**", "/static/**",
                        "/img/**").permitAll() // Public paths
                .anyRequest().authenticated() // Everything else requires login
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                // Never use HTTP Basic — it triggers the browser's native username/password popup.
                if ("true".equalsIgnoreCase(request.getHeader("HX-Request"))) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("HX-Redirect", request.getContextPath() + "/login");
                    return;
                }
                new LoginUrlAuthenticationEntryPoint("/login").commence(request, response, authException);
            }))
            .formLogin(form -> form
                .loginPage("/login")               // Points to your custom GET controller
                .loginProcessingUrl("/login")      // The POST URL Spring Security handles automatically
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .addFilterBefore(demoAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Essential for hashing passwords
    }
}
