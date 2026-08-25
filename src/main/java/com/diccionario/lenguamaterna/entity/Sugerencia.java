package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencias")
public class Sugerencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sugerencia")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "palabra_sugerida", nullable = false, length = 100)
    private String palabraSugerida;

    @Column(name = "posible_traduccion", length = 150)
    private String posibleTraduccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_lengua")
    private Lengua lengua;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Sugerencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getPalabraSugerida() { return palabraSugerida; }
    public void setPalabraSugerida(String palabraSugerida) { this.palabraSugerida = palabraSugerida; }
    public String getPosibleTraduccion() { return posibleTraduccion; }
    public void setPosibleTraduccion(String posibleTraduccion) { this.posibleTraduccion = posibleTraduccion; }
    public Lengua getLengua() { return lengua; }
    public void setLengua(Lengua lengua) { this.lengua = lengua; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
