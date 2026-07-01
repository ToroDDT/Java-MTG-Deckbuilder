package com.example.mtg_deckbuilder.config;
import com.example.mtg_deckbuilder.security.DemoAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

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
                .httpBasic(Customizer.withDefaults())
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
