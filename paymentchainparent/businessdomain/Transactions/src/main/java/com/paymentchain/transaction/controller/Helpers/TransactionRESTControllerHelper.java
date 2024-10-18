package com.paymentchain.transaction.controller.Helpers;

import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.repository.TransactionRepository;
import java.util.List;


public class TransactionRESTControllerHelper {
    
    public static List<Transaction> getFromRepository(
            TransactionRepository repository,
            String iban) {
        return repository.findByIbanAccount(iban);
    }
    
    public static Transaction getFromRepository(
            TransactionRepository repository,
            long id) {
        return repository.findById(id).get(); 
    }
}
