package com.myproject.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.myproject.webclient.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CustomerService {

    Flux<String> listCustomers();
    Flux<Map<String, Object>> listCustomersMap();
    Flux<JsonNode> listCustomersJsonNode();
    Flux<CustomerDTO> listCustomerDtos();
    Mono<CustomerDTO> getCustomerById(String customerId);
    Flux<CustomerDTO> getCustomerByName(String customerName);
    Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO);
    Mono<CustomerDTO> updateCustomer(CustomerDTO customerDTO);
    Mono<CustomerDTO> patchCustomer(String customerId, CustomerDTO customerDTO);
    Mono<Void> deleteCustomer(String customerId);
}
