package us.sportsanalytics.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import us.sportsanalytics.backend.models.dto.workspace.UserWorkspaceDto;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceRepository;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceUserRepository;
import us.sportsanalytics.backend.security.jwt.JwtAuthenticationFilter;
import us.sportsanalytics.backend.security.jwt.JwtService;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.security.workspace.WorkspaceContextFilter;

@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public WorkspaceContextFilter workspaceContextFilter(WorkspaceUserRepository workspaceUserRepository,
            WorkspaceRepository workspaceRepository, WorkspaceContext workspaceContext) {
        return new WorkspaceContextFilter(workspaceUserRepository, workspaceRepository, workspaceContext);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            WorkspaceContextFilter workspaceContextFilter) throws Exception {

        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/workspaces/**").authenticated()
                        .requestMatchers("/api/tables/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(workspaceContextFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
