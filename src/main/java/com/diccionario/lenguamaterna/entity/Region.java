package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "regiones")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Long id;

    @Column(name = "nombre_region", nullable = false, unique = true, length = 100)
    private String nombre;

    public Region() {}
    public Region(String nombre) { this.nombre = nombre; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
