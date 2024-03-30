package com.myproject.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.myproject.webclient.model.BeerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class BeerClientImpl implements BeerClient {
    private final WebClient webClient;
    private final String BEER_PATH = "/api/v3/beer";
    private final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    public BeerClientImpl(WebClient.Builder webClientBuilder)  {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<String> listBeers() {
        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(String.class);
    }

    @Override
    public Flux<Map<String, Object>> listBeerMap() {

        return webClient.get().uri(BEER_PATH)
                .retrieve().bodyToFlux(Map.class)
                .map(map -> (Map<String, Object>) map);
    }

    @Override
    public Flux<JsonNode> listBeersJsonNode() {

        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<BeerDTO> listBeerDtos() {

        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> getBeerById(String beerId) {

        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(beerId))
                .retrieve()
                .bodyToMono(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getBeerByStyle(String beerStyle) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BEER_PATH)
                        .queryParam("beerStyle", beerStyle).build())
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {

        return webClient.post()
                .uri(BEER_PATH)
                .body(Mono.just(beerDTO), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> Mono.just(voidResponseEntity
                        .getHeaders().get("Location").get(0)))
                .map(path -> path.split("/")[path.split("/").length - 1])
                .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDTO> updateBeer(BeerDTO beerDTO) {

        return webClient.put().uri(BEER_PATH_ID, beerDTO.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerDTO.getId()));
    }

    @Override
    public Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO) {

        return webClient.patch().uri(BEER_PATH_ID, beerId)
                .body(Mono.just(beerDTO), BeerDTO.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerId));
    }

    @Override
    public Mono<Void> deleteBeer(String beerId) {

        return webClient.delete().uri(BEER_PATH_ID, beerId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
