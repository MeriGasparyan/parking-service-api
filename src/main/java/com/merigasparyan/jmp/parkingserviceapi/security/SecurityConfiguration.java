package com.merigasparyan.jmp.parkingserviceapi.security;

import com.merigasparyan.jmp.parkingserviceapi.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint
                                         ) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .authenticationProvider(this.authenticationProvider())
                .addFilterBefore(this.authorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
