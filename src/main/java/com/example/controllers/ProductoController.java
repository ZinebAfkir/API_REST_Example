package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Producto;
import com.example.services.ProductoService;
import com.example.utilities.FileUploadUtil;

import jakarta.validation.Valid;


@RestController //Para que todas las peticiones que devuelva sea JSON 
//En API Rest lo que se solicita o lo que se gestiona son recursos y en dependencia del verbo http que se use, 
//será una petición u otra 

@RequestMapping("/productos") //Va gestionar o responder al recurso producto 

public class ProductoController {
    
    // Posteriormente a la capa de servicios quiero que me devuelva una lista de productos 

    //Quiero un metodo que me devuelva una lista de productos con paginacion o no
    @Autowired
    private ProductoService productoService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * El metodo siguiente va a responder a una peticion (request) del tipo: 
     * http://localhost:8080/productos?page=3&size=4  una peticion que le pasas un page y un tamaño
     * es decir, que tiene que ser capaz de devolver un listado de productos paginados o no, pero en cualquier caso ordenado
     * por un criterio(nombre, descripcion, etc)
     * La peticion anterior implica @RequestParam
     * 
     * /mientras que una peticion de tipo productos/3 =>@PathVariable
     */ 

    @GetMapping //Le voy a hacer la peticion por get
     public ResponseEntity<List<Producto>> findAll(@RequestParam(name = "page", required = false) Integer page,
                                                   @RequestParam(name = "size", required = false) Integer size) {

            ResponseEntity<List<Producto>> responseEntity = null;

            List<Producto> productos = new ArrayList<>(); //los productos van a ser una lista de productos 
            Sort sortByName = Sort.by("nombre"); //voy a ordenar mis productos por orden alfabetico 

            //Primero tengo que comprobar si hay paginas y el tamaño
           
            if(page != null && size != null ) {// si la primera condicion  del null no se cumple el && no comprueba la segunda  
            
                 //Con paginacion y ordenamiento

                try {
            
                   Pageable pageable = PageRequest.of(page, size, sortByName); //me interesa que este ordenado por eso esta el sort
                   Page<Producto> productosPaginados = productoService.findAll(pageable); //Antes de invocar el  metodo, tengo que crear antes  el pageable
                   productos = productosPaginados.getContent();  //a partir de productos paginados obtener una lista de productos 
                   responseEntity = new  ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

                } catch (Exception e) {
                    
                    responseEntity = new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

            } else{
                //Sin paginacion pero ordenado igualmente 
            
                try {

                    productos = productoService.findAll(sortByName);
                    responseEntity = new  ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
                    
                } catch (Exception e) {

                    responseEntity = new  ResponseEntity<>(HttpStatus.NO_CONTENT);
                    
                }
            
            }
            
            
            
            
            
            return responseEntity;
     }

     /**
      * Metodo que recupera un producto por el id
      */

      @GetMapping("/{id}") //para mostrar es get y para guardar es post 
      public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id){

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>(); //siendo clave el mensaje y el valor un objeto 



        try {
            
            Producto producto = productoService.findById(id);

            if(producto != null){
                String successMessage = "Se ha encontrado el producto con id:" + id; //mensaje de exito
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("producto", producto);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
           
            } else{
            String errorMessage = "No se ha encontrado el producto con id:" + id; 
            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);

            }
           

        } catch (Exception e) {
  
            String errorGrave = "Error grave"; 
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            
        }
        return responseEntity;
      }
      
    // Guardar (Persistir), un producto, con su presentacion en la base de datos
    // Para probarlo con POSTMAN: Body -> form-data -> producto -> CONTENT TYPE ->
    // application/json
    // no se puede dejar el content type en Auto, porque de lo contrario asume
    // application/octet-stream
    // y genera una exception MediaTypeNotSupported

        
       @PostMapping ( consumes = "multipart/form-data")
       @Transactional 
       
       //El metodo sigiuente recibe un producto
       public ResponseEntity<Map<String, Object>> insert(
        @Valid 
        @RequestPart(name = "producto") Producto producto, 
        BindingResult result, 
        @RequestPart(name = "file") MultipartFile file) throws IOException { //el nombre file que le damos aqui es el que tenemos que escribir en el postMan
 
        
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        //Antes de intentar guaradr el producto en la BBDD, primero hay que comprobar si hay errores en el producto recibido
        //siendo result hace referencia a los mensajes de por ejemplo "nos estamos quedando sin stock" que estan en la clase Producto 
        if(result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>(); //aqui creamos una lista de string dnd van a estar los mensajes de error


            for(ObjectError error : result.getAllErrors()) { //el metodo devuelve una lista de Objetos de Error 
                 errorMessages.add(error.getDefaultMessage()); //siendo el mensaje por defecto los mensajes que hemos creado en la clase Producto
        }
    
        responseAsMap.put("errores", errorMessages);
        responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
        return responseEntity;
    
    }
    //SI NO HAY ERRORES, ENTONCES PERSISTIMOS EL PRODUCTO pero previamente tenemos que comprobar si nos han 
    // enviado una imagen o un archivo
    if(! file.isEmpty()){
       String fileCode = fileUploadUtil.saveFile(file.getOriginalFilename(), file);
       producto.setImagenProducto(fileCode+ "-" + file.getOriginalFilename());
    
    }

    Producto productoDB = productoService.save(producto);

   try {
    if(productoDB != null){
        String mensaje = "El producto se ha creado correctamente";
        responseAsMap.put("mensaje", mensaje);
        responseAsMap.put("producto", productoDB);
        responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);
    }

    else{
        //En caso que no se haya creado el prodcuto
    }
   } catch (DataAccessException e) {

    String errorGrave = "Ha tenido lugar un error grave, y la causa mas probable puede ser" + e.getMostSpecificCause();
    // EL METODO getMostSpecificCause() es el que nos aparece como "caused by" en la terminal, te explica que es lo mas especifico que ha generado error 
    responseAsMap.put("errorGrave", errorGrave);
    responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
   }
        return responseEntity;
       
    
    }


       /**
       * El metodo siguiente actualiza un producto en la base de datos 
       */
        
       @PutMapping("/{id}")  //le pasamos el id del prodcuto y para actualizar usamos el @Put...
       @Transactional 
       
       
       public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Producto producto, 
                                                                BindingResult result,
                                                                @PathVariable(name = "id") Integer id) { 
 
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        //Primero comprobar si hay errores en el producto recibido
        
        if(result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>(); 

            for(ObjectError error : result.getAllErrors()) {  
                 errorMessages.add(error.getDefaultMessage()); 
        }
    
        responseAsMap.put("errores", errorMessages);
        responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
        return responseEntity;
    
    }
    //SI NO HAY ERRORES, ENTONCES PERSISTIMOS EL PRODUCTO
    // Vinculando previamente el id que se recibe con el producto 
    producto.setId(id);
    Producto productoDB = productoService.save(producto);

   try {
    if(productoDB != null){
        String mensaje = "El producto se ha actualizado correctamente";
        responseAsMap.put("mensaje", mensaje);
        responseAsMap.put("producto", productoDB);
        responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
    }

    else{
        //En caso que no se haya creado el prodcuto
    }
   } catch (DataAccessException e) {

    String errorGrave = "Ha tenido lugar un error grave, y la causa mas probable puede ser" + e.getMostSpecificCause();
    responseAsMap.put("errorGrave", errorGrave);
    responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
   }
        return responseEntity;
       
    
    }


    /**
       * El metodo siguiente elimina un producto de la base de datos 
       */
        
       @DeleteMapping("/{id}")  //recibe el id del producto que queremos borrar
       @Transactional 
       
       //Un metodo que elimime no devuelve nada y por tanto no necesitamos un responseAsMap
       //el ResponseEntity responde a los mensajes 

       public ResponseEntity <String> delete(@PathVariable(name = "id") Integer id) { 
                                                            
        ResponseEntity<String> responseEntity = null;

        try {
            //Primero recuperamos(encontramos) el producto antes de eliminarlo
            Producto producto = productoService.findById(id);
            
            if(producto != null){ //si elproducto existe lo elimnamos entonces
                productoService.delete(producto);
                responseEntity = new ResponseEntity<String>("El producto se ha borrado correctamente", HttpStatus.OK);
                
            } else{
              responseEntity = new ResponseEntity<String>("No existe el producto", HttpStatus.NOT_FOUND);
         
            }

        } catch (DataAccessException e) {
            e.getMostSpecificCause();
            responseEntity = new ResponseEntity<String>("Error fatal", HttpStatus.INTERNAL_SERVER_ERROR);
         
            
        }
        
        return responseEntity;
        
        
       
    }


















    /**
     * El metodo siguiente es de ejemplo para entender el formato JSON, no tiene nada que ver en si con el proyecto
     */

    //  @GetMapping //con el get se devolvera una lista de nombres de tipo JASON
    //  public List<String> nombres(){ //me devuelve una lista de tipo string 

    //     List<String> nombres = Arrays.asList("Zineb" , "Elisabet" , "Irene"

    //     );

    //     return nombres;
     

}


