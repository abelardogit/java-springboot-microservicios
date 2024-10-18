package com.paymentchain.product.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.product.controller.Helpers.ProductRESTControllerHelper;
import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repository.ProductRepository;

@RestController
@RequestMapping("/product")
public class ProductRestController {
    
    @Autowired
    ProductRepository productRepository;
    
    @GetMapping()
    public List<Product> list() {
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Product get(@PathVariable("id") long id) {
        return productRepository.findById(id).get();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Product input) {
        Product find = productRepository.findById(id).get();
        if (find != null) {
            find.setCode(input.getCode());
            find.setName(input.getName());
        }
        
        Product save = productRepository.save(find);
        return ResponseEntity.ok(save);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) {
        Product save = productRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Optional<Product> optionalProduct = ProductRESTControllerHelper.getProductFromRepository(productRepository, id);
        if (optionalProduct.get() != null) {
            productRepository.delete(optionalProduct.get());
        }
        return ResponseEntity.ok().build();
         
    }
    
}
