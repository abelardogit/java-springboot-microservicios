package com.paymentchain.customer.controller;


import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import com.paymentchain.customer.controller.Helpers.CustomerRESTControllerHelper;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;


@RestController
@RequestMapping("/customer/v1")
public class CustomerRestController {
    
    private final String URL_PRODUCTS = "http://BUSINESSDOMAIN-PRODUCT/product";
    private final String URL_TRANSACTIONS = "http://localhost:8083/transaction";
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    /*
    private final WebClient.Builder webClientBuilder;
    
    public CustomerRestController(WebClient.Builder aBuilder)
    {
        this.webClientBuilder = aBuilder;
    }
    */
    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });
    
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
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        input.getProducts().forEach(p -> p.setCustomer(input));
        Customer save = customerRepository.save(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
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
         Customer customerByCode = customerRepository.findByCode(code);
         List<CustomerProduct> customerProducts = customerByCode.getProducts();
         customerProducts.forEach(p -> {
             String productName = getProductName(p.getId());
             p.setProductName(productName);
         });
         /*
         String customerIBAN = customerByCode.getIBAN();
         List<?> transactions = getTransactions(customerIBAN);
         customerByCode.setTransactions(transactions);
*/
        return customerByCode;

    }
    
    private String getProductName(long id) {
        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(client);
        WebClient build = webClientBuilder.clientConnector(reactorClientHttpConnector)
                .baseUrl(URL_PRODUCTS)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", URL_PRODUCTS))
        .build();
        
        JsonNode productResponse = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        if (productResponse != null) {
            return productResponse.get("name").asText();
        }
        
        return "";
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
}
