package com.diccionario.lenguamaterna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "palabras", indexes = {
    @Index(name = "idx_palabra_espanol", columnList = "palabra_espanol"),
    @Index(name = "idx_palabra_categoria", columnList = "categoria")
})
public class Palabra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_palabra")
    private Long id;

    @Column(name = "palabra_espanol", nullable = false, length = 120)
    private String espanol;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(nullable = false, length = 500)
    private String significado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_lengua", nullable = false)
    private Lengua lengua;

    public Palabra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEspanol() { return espanol; }
    public void setEspanol(String espanol) { this.espanol = espanol; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getSignificado() { return significado; }
    public void setSignificado(String significado) { this.significado = significado; }
    public Lengua getLengua() { return lengua; }
    public void setLengua(Lengua lengua) { this.lengua = lengua; }
}
