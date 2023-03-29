package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dao.ProductoDao;
import com.example.entities.Producto;

@Service //para llamar a los beans 

public class ProductoServiceImpl implements ProductoService {

    //Como los metodos van a llamar al DAO entonces tengo que insertarlo aqui con el @Autowired para que el Spring lo "inserta"
    @Autowired //el Autowired resuelve una dependencia, tambien se puede resolver por constructores 
    private ProductoDao productoDao;

    @Override
    public List<Producto> findAll(Sort sort) {
       return productoDao.findAll(sort);
    }

    @Override
    public Page<Producto> findAll(Pageable pageable) {
        return productoDao.findAll(pageable);
    }

    @Override
    public Producto findById(long id) {
        return productoDao.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        return productoDao.save(producto);
    }

    @Override
    public void delete(Producto producto) {
        
    }
    
}
