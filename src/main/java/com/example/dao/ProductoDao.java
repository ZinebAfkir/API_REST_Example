package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Producto;

public interface ProductoDao extends JpaRepository<Producto, Long> {

     /*
     * Crearemos unas consultas personalizadas para cuando se busque un productoo,
     * se recupere la presentacion conjuntamente con dicho producto, y tambien para
     * recuperar no todos los productos, sino por pagina, es decir, de 10 en 10, de 20
     * en 20, etc.
     * 
     * RECORDEMOS QUE: Cuando hemos creado las relaciones hemos especificado que 
     * la busqueda sea LAZY, para que no se traiga la presentacion siempre que se 
     * busque un producto, porque serian dos consultas, o una consulta con una 
     * subconsulta, que es menos eficiente que lo que vamos a hacer, hacer una sola 
     * consulta relacionando las entidades, y digo las entidades, porque aunque 
     * de la impresión que es una consulta de SQL no consultamos a las tablas de 
     * la base de datos sino a las entidades 
     * (esto se llama HQL (Hibernate Query Language))
     * 
     * Ademas, tambien podremos recuperar el listado de productos de forma ordenada, 
     * por algun criterio de ordenación, como por

   

    /**
     * Vamos a necesitar tres metodos adicionales a los que genera el CRUD repository(interface crud repository), para: 
     * 1.Recuperar una lista de productos ordenados
     * 2.Recuperar listado de productos paginados, es decir, que no traiga todos los productos sino que por ejemplo
     * de 10 en 10, de 20 en 20, etc.
     * 3.Necesitamos una consulta para recuperar las presentaciones con sus productos correspondientes sin tener que realizar
     * una subconsulta qque seria menos eficiente que un join a las entidades utilizando HQL(Hibernate Query Langauge)
     * 
     */
    
   // Primer metodo: uno que me devuelve una lista de productos ordenados

   //La siguiente consulta Le digo dame todos los productos con su presentacion
   //La siguiente consulta no la hemos hecho en mysql y luego pegarla, es decir se parece a una consulta de mysql pero no lo es
   @Query(value = "select  from Producto p left join fetch p.presentacion") //El Query es para mi consulta, siendo Producto es el nombre de mi entidad, mientras que en mysql las tablas se han creado automaticamente como "producto" y "presentacion"

   public List<Producto> findAll(Sort sort); //la consulta anterior devuelve una lista de productos ordenados

  // Segundo metodo: metodo que recupera una pagina de producto, tiene que devolver un page de productos 

  //La siguiente consulta es la misma que la anterior pero ahora quiero limitar la cantidad de productos 
  @Query(value = "select  from Producto p left join fetch p.presentacion",
      countQuery = "select count(p) from Producto p left join p.presentacion") 
  public Page<Producto> findAll(Pageable pageable); 


  //Tercer metodo: metodo que recupera un producto por el id(y por tanto que me de su presentacion)
  //dnd el id del producto sea igual al id que yo le paso 
  
  @Query(value = "select  from Producto p left join fetch p.presentacion where p.id = :id")
  public List<Producto> findById(long id);


}
