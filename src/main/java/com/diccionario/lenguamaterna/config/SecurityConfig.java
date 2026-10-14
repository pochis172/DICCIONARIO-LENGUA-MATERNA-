package com.diccionario.lenguamaterna.config;

import com.diccionario.lenguamaterna.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // La API usa JWT, por eso no usamos CSRF
                .csrf(csrf ->
                        csrf.disable()
                )

                // No usamos formulario de login de Spring
                .formLogin(form ->
                        form.disable()
                )

                // No usamos autenticacion Basic
                .httpBasic(basic ->
                        basic.disable()
                )

                // JWT trabaja sin sesiones
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // MANEJO DE ERRORES DE SEGURIDAD
                .exceptionHandling(exception ->
                        exception

                                // Sin autenticacion = 401
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {

                                            response.setStatus(401);
                                            response.setContentType(
                                                    "application/json"
                                            );

                                            response.getWriter().write(
                                                    """
                                                    {
                                                      "error": "No autenticado. Debes enviar un token JWT valido."
                                                    }
                                                    """
                                            );
                                        }
                                )

                                // Autenticado pero sin permisos = 403
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {

                                            response.setStatus(403);
                                            response.setContentType(
                                                    "application/json"
                                            );

                                            response.getWriter().write(
                                                    """
                                                    {
                                                      "error": "No tienes permisos para realizar esta accion."
                                                    }
                                                    """
                                            );
                                        }
                                )
                )

                // RUTAS
                .authorizeHttpRequests(auth ->
                        auth

                                // Registro y login publicos
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login"
                                )
                                .permitAll()

                                // Diccionario publico
                                .requestMatchers(
                                        "/api/diccionario/**"
                                )
                                .permitAll()

                                // Audios publicos
                                .requestMatchers(
                                        "/uploads/**"
                                )
                                .permitAll()

                                // Las demas rutas necesitan JWT
                                .anyRequest()
                                .authenticated()
                )

                // Revisar JWT antes del filtro normal de Spring
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}