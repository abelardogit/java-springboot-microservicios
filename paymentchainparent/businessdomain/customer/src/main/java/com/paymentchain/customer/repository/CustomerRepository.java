/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package com.paymentchain.customer.repository;

import com.paymentchain.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author abela
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c from Customer c where c.code = ?1")
    public Customer findByCode(String aCode);
    
    /*@Query("SELECT i from Customer c where c.IBAN = ?1")
    public Customer findByIBAN(String anIBAN);*/
}
