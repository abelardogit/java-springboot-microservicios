/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.exceptions;

import com.paymentchain.customer.common.StandarizedApiExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownHostException(Exception exc)
    {
        StandarizedApiExceptionResponse saer = new StandarizedApiExceptionResponse("atype", "aTitle", "aCode", "aDetail");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(saer);
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> handleBusinessRuleException(BusinessRuleException aBusinessRuleException)
    {
        StandarizedApiExceptionResponse saer = new StandarizedApiExceptionResponse("BUSINESS", "Validation error: Products exist?", aBusinessRuleException.getCode(), aBusinessRuleException.getLocalizedMessage());
        return ResponseEntity.status(aBusinessRuleException.getHttpStatus()).body(saer);
    }
}
