package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Sugerencia;
import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.LenguaRepository;
import com.diccionario.lenguamaterna.repository.SugerenciaRepository;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SuggestionService {
    private final SugerenciaRepository sugerenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LenguaRepository lenguaRepository;

    public SuggestionService(SugerenciaRepository sugerenciaRepository, UsuarioRepository usuarioRepository, LenguaRepository lenguaRepository) {
        this.sugerenciaRepository = sugerenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.lenguaRepository = lenguaRepository;
    }

    @Transactional(readOnly = true)
    public List<SugerenciaResponse> mias(Long usuarioId) {
        return sugerenciaRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SugerenciaResponse crear(Long usuarioId, SugerenciaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        Sugerencia s = new Sugerencia();
        s.setUsuario(usuario);
        aplicar(s, request);
        s.setEstado("PENDIENTE");
        s.setFechaCreacion(LocalDateTime.now());
        return toResponse(sugerenciaRepository.save(s));
    }

    @Transactional
    public SugerenciaResponse editar(Long usuarioId, Long id, SugerenciaRequest request) {
        Sugerencia s = propia(usuarioId, id);
        if (!"PENDIENTE".equalsIgnoreCase(s.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puedes editar sugerencias pendientes.");
        }
        aplicar(s, request);
        return toResponse(sugerenciaRepository.save(s));
    }

    @Transactional
    public void eliminar(Long usuarioId, Long id) {
        Sugerencia s = propia(usuarioId, id);
        if (!"PENDIENTE".equalsIgnoreCase(s.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puedes eliminar sugerencias pendientes.");
        }
        sugerenciaRepository.delete(s);
    }

    private Sugerencia propia(Long usuarioId, Long id) {
        Sugerencia s = sugerenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sugerencia no encontrada."));
        if (!s.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar esta sugerencia.");
        }
        return s;
    }

    private void aplicar(Sugerencia s, SugerenciaRequest request) {
        s.setPalabraSugerida(request.palabraSugerida().trim());
        s.setPosibleTraduccion(request.posibleTraduccion() == null ? null : request.posibleTraduccion().trim());
        s.setDescripcion(request.descripcion() == null ? null : request.descripcion().trim());
        if (request.lenguaId() != null) {
            Lengua lengua = lenguaRepository.findById(request.lenguaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengua no válida."));
            s.setLengua(lengua);
        } else {
            s.setLengua(null);
        }
    }

    public SugerenciaResponse toResponse(Sugerencia s) {
        return new SugerenciaResponse(
                s.getId(), s.getUsuario().getId(), s.getUsuario().getNombre(), s.getPalabraSugerida(),
                s.getPosibleTraduccion(), s.getLengua() == null ? null : s.getLengua().getId(),
                s.getLengua() == null ? null : s.getLengua().getNombre(), s.getDescripcion(),
                s.getEstado(), s.getFechaCreacion()
        );
    }
}
