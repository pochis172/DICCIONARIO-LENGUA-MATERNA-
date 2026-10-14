package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.AuthUtil;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.service.DictionaryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/diccionario")
public class DictionaryController {
    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/buscar")
    @PreAuthorize("permitAll()")
    public List<PalabraResponse> buscar(@RequestParam String q,
                                        @RequestParam(required = false) Long lenguaId,
                                        Authentication authentication) {
        return dictionaryService.buscar(q, lenguaId, AuthUtil.optionalUserId(authentication));
    }

    @GetMapping("/palabras")
    @PreAuthorize("permitAll()")
    public List<PalabraResponse> palabras(@RequestParam(required = false) String q,
                                          @RequestParam(required = false) String categoria,
                                          Authentication authentication) {
        return dictionaryService.listar(q, categoria, AuthUtil.optionalUserId(authentication));
    }

    @GetMapping("/palabras/{id}")
    @PreAuthorize("permitAll()")
    public PalabraResponse palabra(@PathVariable Long id, Authentication authentication) {
        return dictionaryService.obtener(id, AuthUtil.optionalUserId(authentication));
    }

    @GetMapping("/categorias")
    @PreAuthorize("permitAll()")
    public List<String> categorias() {
        return dictionaryService.categorias();
    }

    @GetMapping("/lenguas")
    @PreAuthorize("permitAll()")
    public List<LenguaResponse> lenguas() {
        return dictionaryService.lenguas();
    }
}
