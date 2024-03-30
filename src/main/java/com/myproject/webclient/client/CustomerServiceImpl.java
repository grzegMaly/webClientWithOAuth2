package com.myproject.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.myproject.webclient.model.BeerDTO;
import com.myproject.webclient.model.CustomerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final WebClient webClient;
    private final String CUSTOMER_PATH = "/api/v3/customer";
    private final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    public CustomerServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<String> listCustomers() {

        return webClient.get().uri(CUSTOMER_PATH)
                .retrieve().bodyToFlux(String.class);
    }

    @Override
    public Flux<Map<String, Object>> listCustomersMap() {

        return webClient.get().uri(CUSTOMER_PATH)
                .retrieve().bodyToFlux(Map.class)
                .map(map -> (Map<String, Object>) map);
    }

    @Override
    public Flux<JsonNode> listCustomersJsonNode() {

        return webClient.get().uri(CUSTOMER_PATH)
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<CustomerDTO> listCustomerDtos() {

        return webClient.get().uri(CUSTOMER_PATH)
                .retrieve()
                .bodyToFlux(CustomerDTO.class);
    }

    @Override
    public Mono<CustomerDTO> getCustomerById(String customerId) {

        return webClient.get().uri(uriBuilder -> uriBuilder.path(CUSTOMER_PATH_ID).build(customerId))
                .retrieve()
                .bodyToMono(CustomerDTO.class);
    }

    @Override
    public Flux<CustomerDTO> getCustomerByName(String customerName) {

        return webClient.get().uri(uriBuilder -> uriBuilder.path(CUSTOMER_PATH)
                .queryParam("customerName", customerName).build())
                .retrieve()
                .bodyToFlux(CustomerDTO.class);
    }

    @Override
    public Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO) {

        return webClient.post().uri(CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> Mono.just(voidResponseEntity
                        .getHeaders().get("Location").get(0)))
                .map(path -> path.split("/")[path.split("/").length - 1])
                .flatMap(this::getCustomerById);
    }

    @Override
    public Mono<CustomerDTO> updateCustomer(CustomerDTO customerDTO) {

        return webClient.put().uri(CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getCustomerById(customerDTO.getId()));
    }

    @Override
    public Mono<CustomerDTO> patchCustomer(String customerId, CustomerDTO customerDTO) {

        return webClient.patch().uri(CUSTOMER_PATH_ID, customerId)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getCustomerById(customerId));
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {

        return webClient.delete().uri(CUSTOMER_PATH_ID, customerId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
