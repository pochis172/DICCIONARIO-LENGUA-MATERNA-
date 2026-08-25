package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplos")
public class Ejemplo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_palabra", nullable = false)
    private Palabra palabra;

    @Column(nullable = false, length = 255)
    private String ejemplo;

    public Ejemplo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Palabra getPalabra() { return palabra; }
    public void setPalabra(Palabra palabra) { this.palabra = palabra; }
    public String getEjemplo() { return ejemplo; }
    public void setEjemplo(String ejemplo) { this.ejemplo = ejemplo; }
}
