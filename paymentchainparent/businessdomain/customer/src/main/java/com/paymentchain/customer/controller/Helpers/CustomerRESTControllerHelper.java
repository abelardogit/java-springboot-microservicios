/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.controller.Helpers;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repository.CustomerRepository;
import java.util.Optional;

/**
 *
 * @author abela
 */
public class CustomerRESTControllerHelper {
    
    public static Optional<Customer> getCustomerFromRepository(
            CustomerRepository customerRepository,
            long id) {
        return customerRepository.findById(id); 
    }
}
