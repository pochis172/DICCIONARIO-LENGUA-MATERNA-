package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.service.DictionaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diccionario")
public class DictionaryController {
    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/buscar")
    public List<PalabraResponse> buscar(@RequestParam String q,
                                        @RequestParam(required = false) Long lenguaId,
                                        HttpSession session) {
        return dictionaryService.buscar(q, lenguaId, SessionUtil.optionalUserId(session));
    }

    @GetMapping("/palabras")
    public List<PalabraResponse> palabras(@RequestParam(required = false) String q,
                                          @RequestParam(required = false) String categoria,
                                          HttpSession session) {
        return dictionaryService.listar(q, categoria, SessionUtil.optionalUserId(session));
    }

    @GetMapping("/palabras/{id}")
    public PalabraResponse palabra(@PathVariable Long id, HttpSession session) {
        return dictionaryService.obtener(id, SessionUtil.optionalUserId(session));
    }

    @GetMapping("/categorias")
    public List<String> categorias() {
        return dictionaryService.categorias();
    }

    @GetMapping("/lenguas")
    public List<LenguaResponse> lenguas() {
        return dictionaryService.lenguas();
    }
}
