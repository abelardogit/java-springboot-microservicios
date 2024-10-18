package com.paymentchain.customer.controller.Helpers;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exceptions.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

@Service
public class CustomerRESTControllerHelper {
    private static final String URL_PRODUCTS = "http://BUSINESSDOMAIN-PRODUCT/product";
    private static final String URL_TRANSACTIONS = "http://localhost:8083/transaction";
    
    @Autowired
    private static CustomerRepository customerRepository;
    
    @Autowired
    private static WebClient.Builder webClientBuilder;
    
    private static HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });
    

            public static Optional<Customer> getCustomerFromRepository(
            CustomerRepository customerRepository,
            long id) {
        return customerRepository.findById(id); 
    }
    
    public static Customer getByCode(@RequestParam("code") String code) throws UnknownHostException {
        Customer customerByCode = customerRepository.findByCode(code);
        List<CustomerProduct> customerProducts = customerByCode.getProducts();
        customerProducts.forEach(p -> {
            String productName;
            try {
                productName = getProductName(p.getId());
            } catch(UnknownHostException uhex) {
                productName = "";
            }
            p.setProductName(productName);
        });
        /*
        String customerIBAN = customerByCode.getIBAN();
        List<?> transactions = getTransactions(customerIBAN);
        customerByCode.setTransactions(transactions);
*/
       return customerByCode;

    }
    
    private static String getProductName(long id) throws UnknownHostException {
        JsonNode productResponse;
        try {
            ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(client);
            WebClient build = webClientBuilder.clientConnector(reactorClientHttpConnector)
                    .baseUrl(URL_PRODUCTS)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", URL_PRODUCTS))
            .build();
        
            productResponse = build.method(HttpMethod.GET).uri("/" + id)
                   .retrieve().bodyToMono(JsonNode.class).block();
            if (productResponse == null) {
                throw new UnknownHostException("Response was not found!");
            }
        } catch (WebClientResponseException aWebClientResponseException) {
            if (aWebClientResponseException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                return "";
            }
            throw new UnknownHostException(aWebClientResponseException.getLocalizedMessage());
        }
        
        return productResponse.get("name").asText();
    }
    
    
    private List<?> getTransactions(String ibanAccount) {
        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(client);
        WebClient build = webClientBuilder.clientConnector(reactorClientHttpConnector)
                .baseUrl(URL_TRANSACTIONS)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
        
        Optional<List<?>> transactionsOptional = Optional.ofNullable(build.method(HttpMethod.GET)
        .uri(uriBuilder -> uriBuilder
                .path("/customer/transactions")
                .queryParam("ibanAccount", ibanAccount)
                .build())
        .retrieve()
        .bodyToFlux(Object.class)
        .collectList()
        .block());       

        return transactionsOptional.orElse(Collections.emptyList());
    }
    
    public static Customer ensureCustomerHasProductsOrFail(@RequestBody Customer aCustomer) throws BusinessRuleException {
        List<CustomerProduct> customerProducts = aCustomer.getProducts();
        boolean nonExistCustomerProducts = (null == customerProducts) || (customerProducts.isEmpty());
        if (nonExistCustomerProducts) {
            throw BusinessRuleException.fromCreate(1025, "No products were provided", "1025", HttpStatus.PRECONDITION_FAILED);
        }
        
        Iterator<CustomerProduct> itCustomerProducts = aCustomer.getProducts().iterator();
        boolean someCustomerProductsNonExist = false;
        while(itCustomerProducts.hasNext() && !someCustomerProductsNonExist) {
            CustomerProduct aCustomerProduct = itCustomerProducts.next();
            someCustomerProductsNonExist = (null != aCustomerProduct);
            if (!someCustomerProductsNonExist) {
                aCustomerProduct.setCustomer(aCustomer);
            }
        }
        
        if (someCustomerProductsNonExist) {
            throw BusinessRuleException.fromCreate(1026, "Some product given no exist", "1026", HttpStatus.PRECONDITION_FAILED);
        }
            
        return customerRepository.save(aCustomer);
    }
    
}
