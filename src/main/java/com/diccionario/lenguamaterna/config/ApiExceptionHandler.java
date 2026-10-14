package com.diccionario.lenguamaterna.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    // ERRORES CONTROLADOS DE LA APLICACION
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> responseStatus(
            ResponseStatusException ex
    ) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        error(
                                ex.getReason() == null
                                        ? "Error"
                                        : ex.getReason()
                        )
                );
    }


    // ERRORES DE VALIDACION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(
            MethodArgumentNotValidException ex
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(e ->
                                e.getField()
                                        + ": "
                                        + e.getDefaultMessage()
                        )
                        .orElse(
                                "Datos inválidos."
                        );

        return ResponseEntity
                .badRequest()
                .body(
                        error(message)
                );
    }


    // USUARIO AUTENTICADO PERO SIN PERMISOS
    // Ejemplo:
    // EDITOR intentando entrar a una ruta de SUPER_USUARIO
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDenied(
            AccessDeniedException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        error(
                                "No tienes permisos para realizar esta accion."
                        )
                );
    }


    // CUALQUIER OTRO ERROR NO CONTROLADO
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(
            Exception ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        error(
                                "Ocurrió un error interno: "
                                        + ex.getMessage()
                        )
                );
    }


    // FORMATO COMUN DE LOS ERRORES
    private Map<String, Object> error(
            String message
    ) {

        Map<String, Object> map =
                new LinkedHashMap<>();

        map.put(
                "timestamp",
                LocalDateTime.now()
        );

        map.put(
                "error",
                message
        );

        return map;
    }
}