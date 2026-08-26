package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lenguas")
public class Lengua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lengua")
    private Long id;

    @NotBlank(message = "El nombre de la lengua es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @NotNull(message = "La región es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_region", nullable = false)
    private Region region;

    @Size(max = 100, message = "La familia lingüística no puede superar 100 caracteres")
    @Column(name = "familia_linguistica", length = 100)
    private String familiaLinguistica;

    public Lengua() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getFamiliaLinguistica() {
        return familiaLinguistica;
    }

    public void setFamiliaLinguistica(String familiaLinguistica) {
        this.familiaLinguistica = familiaLinguistica;
    }
}