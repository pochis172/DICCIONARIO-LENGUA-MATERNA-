package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.service.DictionaryService;
import com.diccionario.lenguamaterna.service.UserService;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diccionario")
public class DictionaryController {

    private final DictionaryService dictionaryService;
    private final UserService userService;

    public DictionaryController(
            DictionaryService dictionaryService,
            UserService userService
    ) {
        this.dictionaryService = dictionaryService;
        this.userService = userService;
    }


    // BUSCAR PALABRAS
    // Ruta publica
    @GetMapping("/buscar")
    public List<PalabraResponse> buscar(
            @RequestParam String q,
            @RequestParam(required = false) Long lenguaId,
            Authentication authentication
    ) {

        Long usuarioId =
                obtenerUsuarioIdOpcional(authentication);

        return dictionaryService.buscar(
                q,
                lenguaId,
                usuarioId
        );
    }


    // LISTAR PALABRAS
    // Ruta publica
    @GetMapping("/palabras")
    public List<PalabraResponse> palabras(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            Authentication authentication
    ) {

        Long usuarioId =
                obtenerUsuarioIdOpcional(authentication);

        return dictionaryService.listar(
                q,
                categoria,
                usuarioId
        );
    }


    // OBTENER UNA PALABRA
    // Ruta publica
    @GetMapping("/palabras/{id}")
    public PalabraResponse palabra(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long usuarioId =
                obtenerUsuarioIdOpcional(authentication);

        return dictionaryService.obtener(
                id,
                usuarioId
        );
    }


    // LISTAR CATEGORIAS
    // Ruta publica
    @GetMapping("/categorias")
    public List<String> categorias() {

        return dictionaryService.categorias();
    }


    // LISTAR LENGUAS
    // Ruta publica
    @GetMapping("/lenguas")
    public List<LenguaResponse> lenguas() {

        return dictionaryService.lenguas();
    }


    // OBTENER USUARIO OPCIONAL DESDE JWT
    private Long obtenerUsuarioIdOpcional(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return null;
        }

        return userService.obtenerIdPorCorreo(
                authentication.getName()
        );
    }
}