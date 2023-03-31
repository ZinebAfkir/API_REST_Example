package com.example.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Presentacion implements Serializable {

    private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private long id;
    
    private String nombre;
    private String descripcion;

    
    //Relacion entre las 2 tablas Producto y Presentacion 
    //Muchos productos pueden tener la misma presentacion

    //El mapped by va en el lado de "muchos" que en nuestro caso es son muchos productos en la clase presentacion
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "presentacion") 
    @JsonIgnore // eso es para evitar recursividad entre producto y presentación (cuando pides producto llama a presentación,
    // y cuado pides presnetacion llama a producto y asi de forma recursiva por eso metemos el @Json)
    private List<Producto> productos;
}
