package com.paymentchain.transaction.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Data;


@Data
@Entity
public class Transaction {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String reference;
    private String ibanAccount;
    private Date date;
    private double amount;
    private double fee;
    private String description;
    private String status;
    private String channel;
}
