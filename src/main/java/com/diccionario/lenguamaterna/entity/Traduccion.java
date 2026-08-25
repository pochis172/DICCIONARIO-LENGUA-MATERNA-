package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "traducciones")
public class Traduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_traduccion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_palabra", nullable = false)
    private Palabra palabra;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_lengua", nullable = false)
    private Lengua lengua;

    @Column(nullable = false, length = 150)
    private String traduccion;

    public Traduccion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Palabra getPalabra() { return palabra; }
    public void setPalabra(Palabra palabra) { this.palabra = palabra; }
    public Lengua getLengua() { return lengua; }
    public void setLengua(Lengua lengua) { this.lengua = lengua; }
    public String getTraduccion() { return traduccion; }
    public void setTraduccion(String traduccion) { this.traduccion = traduccion; }
}
