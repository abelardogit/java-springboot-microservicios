package com.paymentchain.customer.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.customer.controller.Helpers.CustomerRESTControllerHelper;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exceptions.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/customer/v1")
public class CustomerRestController {
    
    
    /*
    private final WebClient.Builder webClientBuilder;
    
    public CustomerRestController(WebClient.Builder aBuilder)
    {
        this.webClientBuilder = aBuilder;
    }
    */
    
    
    
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private Environment env;
    /*
    @Value("${custom.activeprofilename}")
    String profile;
    */

   
    
    @GetMapping("/check")
    public String check()
    {
        String property = "custom.activeprofilename";
        return "El valor de " + property +  " es " + env.getProperty(property);
    }
    
    /**
     *
     * @return
     */
    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> findAll = customerRepository.findAll();
        
        boolean isNullList = (null == findAll);
        boolean isEmpty = true;
        if (!isNullList) 
            isEmpty = findAll.isEmpty();
        
        if (isNullList || isEmpty) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(findAll);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
         Optional<Customer> customer = CustomerRESTControllerHelper.getCustomerFromRepository(customerRepository, id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Customer input) {
        Optional<Customer> optionalcustomer = CustomerRESTControllerHelper.getCustomerFromRepository(customerRepository, id);
        if (optionalcustomer.isPresent()) {
            Customer newcustomer = optionalcustomer.get();
            newcustomer.setName(input.getName());
            newcustomer.setPhone(input.getPhone());
             Customer save = customerRepository.save(newcustomer);
          return new ResponseEntity<>(save, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /**
     *
     * @param aCustomer
     * @return
     * @throws com.paymentchain.customer.exceptions.BusinessRuleException
     */
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer aCustomer) throws BusinessRuleException {
        Customer aCustomerWithProducts = CustomerRESTControllerHelper.ensureCustomerHasProductsOrFail(aCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(aCustomerWithProducts);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Optional<Customer> optionalcustomer = CustomerRESTControllerHelper.getCustomerFromRepository(customerRepository, id);
        if (optionalcustomer.isPresent()) {
            customerRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
         
    }
    
    @GetMapping("/full")
    public Customer getByCode(@RequestParam("code") String code) {
        return  CustomerRESTControllerHelper.getByCode(code);
    }
    
}
