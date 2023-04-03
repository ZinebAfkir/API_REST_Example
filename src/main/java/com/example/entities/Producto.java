package com.example.entities;

import java.io.Serializable;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Producto implements Serializable {

    private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min = 4, max = 25, message = "El nombre tiene que estar entre 4 y 25 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private double precio;

    @Min(value = 0, message = "Nos estamos quedando sin stock")
    private long stock;

    @NotNull
    private String imagenProducto;

    //Relacion entre las 2 tablas Producto y Presentacion 

    //Muchos productos a una sola presentacion 
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) //LAZY Hace que cuando le pides un producto No te trae todas las presentaciones sino que tiene que hacer una consulta para traer o pedir lo que tu quieras, y la consulta la haces tu, el LAZY te trae el producto sin la presentacion 
    private Presentacion presentacion;
}
