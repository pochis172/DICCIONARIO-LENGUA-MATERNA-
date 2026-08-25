package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "audios")
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_palabra", nullable = false)
    private Palabra palabra;

    @Column(name = "url_audio", nullable = false, length = 255)
    private String urlAudio;

    @Column(nullable = false, length = 50)
    private String tipo;

    public Audio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Palabra getPalabra() { return palabra; }
    public void setPalabra(Palabra palabra) { this.palabra = palabra; }
    public String getUrlAudio() { return urlAudio; }
    public void setUrlAudio(String urlAudio) { this.urlAudio = urlAudio; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
