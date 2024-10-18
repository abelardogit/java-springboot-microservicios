package com.paymentchain.customer.exceptions;

import java.util.HashMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class BusinessRuleException extends Exception {
    
    private long id;
    private String code;
    private HttpStatus httpStatus;
    
    private BusinessRuleException(HashMap<String, Object> parameters)
    {
        super((String) parameters.get("message"));
        
        id = (long) parameters.get("id");
        code = (String) parameters.get("code");
        httpStatus = (HttpStatus) parameters.get("status");
    }
    
    public BusinessRuleException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public static BusinessRuleException fromCreate(long id, String message, String code, HttpStatus status)
    {
        HashMap<String, Object> parameters = new HashMap();
        
        parameters.put("id", id);
        parameters.put("message", message);
        parameters.put("code", code);
        parameters.put("status", status);
        
        return new BusinessRuleException(parameters);
    }
    
    
}
