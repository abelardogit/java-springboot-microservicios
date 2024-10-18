package com.paymentchain.customer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paymentchain.customer.common.StandarizedApiExceptionResponse;

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
        StandarizedApiExceptionResponse saer = new StandarizedApiExceptionResponse("BUSINESS", "Validation error", aBusinessRuleException.getCode(), aBusinessRuleException.getLocalizedMessage());
        return ResponseEntity.status(aBusinessRuleException.getHttpStatus()).body(saer);
    }
}
