package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favoritos", uniqueConstraints = @UniqueConstraint(name = "uk_favorito_usuario_palabra", columnNames = {"id_usuario", "id_palabra"}))
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_palabra", nullable = false)
    private Palabra palabra;

    public Favorito() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Palabra getPalabra() { return palabra; }
    public void setPalabra(Palabra palabra) { this.palabra = palabra; }
}
