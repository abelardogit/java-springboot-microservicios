
package com.paymentchain.transaction.repository;

import com.paymentchain.transaction.entities.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t from Transaction t where t.ibanAccount = ?1")
    public List<Transaction> findByIbanAccount(String ibanAccount);
}
