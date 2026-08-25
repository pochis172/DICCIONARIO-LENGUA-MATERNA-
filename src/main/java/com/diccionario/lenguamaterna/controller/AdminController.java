package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraRequest;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.EstadoSugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.service.AdminService;
import com.diccionario.lenguamaterna.service.DictionaryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "wav", "ogg", "m4a");

    private final AdminService adminService;
    private final DictionaryService dictionaryService;

    public AdminController(AdminService adminService, DictionaryService dictionaryService) {
        this.adminService = adminService;
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/palabras")
    public List<PalabraResponse> palabras(@RequestParam(required = false) String q, HttpSession session) {
        SessionUtil.requireAdmin(session);
        return adminService.listarPalabras(q);
    }

    @PostMapping("/palabras")
    public PalabraResponse crearPalabra(@Valid @RequestBody PalabraRequest request, HttpSession session) {
        SessionUtil.requireAdmin(session);
        return adminService.crear(request);
    }

    @PutMapping("/palabras/{id}")
    public PalabraResponse editarPalabra(@PathVariable Long id, @Valid @RequestBody PalabraRequest request, HttpSession session) {
        SessionUtil.requireAdmin(session);
        return adminService.editar(id, request);
    }

    @DeleteMapping("/palabras/{id}")
    public ResponseEntity<Void> eliminarPalabra(@PathVariable Long id, HttpSession session) {
        SessionUtil.requireAdmin(session);
        adminService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sugerencias")
    public List<SugerenciaResponse> sugerencias(HttpSession session) {
        SessionUtil.requireAdmin(session);
        return adminService.sugerencias();
    }

    @PatchMapping("/sugerencias/{id}/estado")
    public SugerenciaResponse estado(@PathVariable Long id, @Valid @RequestBody EstadoSugerenciaRequest request, HttpSession session) {
        SessionUtil.requireAdmin(session);
        return adminService.cambiarEstadoSugerencia(id, request.estado());
    }

    @PostMapping("/audio/upload")
    public Map<String, String> subirAudio(@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        SessionUtil.requireAdmin(session);
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona un archivo de audio.");

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "audio" : file.getOriginalFilename());
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!AUDIO_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido. Usa MP3, WAV, OGG o M4A.");
        }

        Path dir = Path.of("uploads", "audio");
        Files.createDirectories(dir);
        String nombre = UUID.randomUUID() + "." + extension;
        Files.copy(file.getInputStream(), dir.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        return Map.of("url", "/uploads/audio/" + nombre);
    }
}
