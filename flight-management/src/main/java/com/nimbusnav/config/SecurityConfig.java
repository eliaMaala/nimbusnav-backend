package com.nimbusnav.config;

import com.nimbusnav.security.CustomUserDetailsService;
import com.nimbusnav.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login",
                                "/api/auth/register",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/auth/logout",
                                "/api/auth/refresh-token",//  السماح للكل باستخدام التوكن المحدث
                                "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/flights").hasAuthority("ROLE_USER") //  منح الإذن لمستخدمي "USER"
                        .requestMatchers(HttpMethod.GET, "/api/flights/all").hasAuthority("ROLE_ADMIN") //  منح الإذن لمستخدمي "ADMIN"
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasAuthority("ROLE_USER") //  السماح للمستخدم بحذف رحلاته الخاصة
                        //  المستخدم يستطيع جلب الرحلات حسب الحالة
                        .requestMatchers(HttpMethod.GET, "/api/flights/status")
                        .hasAuthority("ROLE_USER")

                        //  المستخدم يستطيع فقط تحديث رحلاته الخاصة - والإداري يستطيع تحديث أي رحلة
                        .requestMatchers(HttpMethod.PATCH, "/api/flights/{id}/status")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        //  تأمين عمليات إدارة الحجوزات
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasAnyRole("USER", "ADMIN") // المستخدمين المسجلين فقط
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasRole("USER") // المستخدم يرى فقط حجوزاته
                        .requestMatchers(HttpMethod.GET, "/api/bookings/{id}").hasAnyRole("USER", "ADMIN") // المستخدم يمكنه رؤية حجزه + الأدمن
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/{id}").hasAnyRole("USER", "ADMIN") // المستخدم يمكنه إلغاء حجزه + الأدمن

                        .requestMatchers("/api/payments/**").authenticated()  //  السماح فقط للمستخدمين المسجلين

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
