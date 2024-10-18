/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.product.controller.Helpers;

import com.paymentchain.product.entities.Product;
import java.util.Optional;
import com.paymentchain.product.repository.ProductRepository;

/**
 *
 * @author abela
 */
public class ProductRESTControllerHelper {
    
    public static Optional<Product> getProductFromRepository(
            ProductRepository productRepository,
            long id) {
        return productRepository.findById(id); 
    }
}
