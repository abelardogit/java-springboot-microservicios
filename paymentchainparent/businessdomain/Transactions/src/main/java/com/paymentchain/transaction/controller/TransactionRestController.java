package com.paymentchain.transaction.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.repository.TransactionRepository;
import com.paymentchain.transaction.controller.Helpers.TransactionRESTControllerHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/transaction")
public class TransactionRestController {
    
    @Autowired
    private TransactionRepository transactionRepository;
    @GetMapping()
    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable("id") long id) {
         return transactionRepository.findById(id).map(x -> ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build());
    }
    
     @GetMapping("/customer/transactions")
    public List<Transaction> get(@RequestParam("ibanAccount") String ibanAccount) {
         return TransactionRESTControllerHelper.getFromRepository(transactionRepository, ibanAccount);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Transaction input) {
        Transaction transaction = TransactionRESTControllerHelper.getFromRepository(transactionRepository, id);
        if (transaction != null) {
            transaction.setReference(input.getReference());
            transaction.setIbanAccount(input.getIbanAccount());
            transaction.setDate(input.getDate());
            transaction.setAmount(input.getAmount());
            transaction.setFee(input.getFee());
            transaction.setDescription(input.getDescription());
            transaction.setStatus(input.getStatus());
            transaction.setChannel(input.getChannel());
            
             Transaction save = transactionRepository.save(transaction);
          return new ResponseEntity<>(save, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Transaction input) {
        Transaction save = transactionRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        Transaction transaction = TransactionRESTControllerHelper.getFromRepository(transactionRepository, id);
        if (transaction != null) {
            transactionRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
         
    }
}
