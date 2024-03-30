package com.myproject.webclient.client;

import com.myproject.webclient.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    AtomicBoolean atomicBoolean;

    @BeforeEach
    void setUp() {
        atomicBoolean = new AtomicBoolean(false);
    }

    @Test
    void listBeers() {

        beerClient.listBeers().subscribe(response -> {
            atomicBoolean.set(true);
            System.out.println(response);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(2)
    void testListBeersGetMap() {

        beerClient.listBeerMap().subscribe(response -> {
            System.out.println(response);
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(3)
    void testGetBeerJson() {

        beerClient.listBeersJsonNode().subscribe(jsonNode -> {
            System.out.println(jsonNode.toPrettyString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(4)
    void testTetBeerDto() {

        beerClient.listBeerDtos().subscribe(dto -> {
            System.out.println(dto.getBeerName());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(5)
    void testGetBeerById() {

        beerClient.listBeerDtos()
                .flatMap(dto -> beerClient.getBeerById(dto.getId()))
                .subscribe(byIdDto -> {
                    System.out.println(byIdDto.getBeerName());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(6)
    void testGetBeerByBeerStyle() {

        beerClient.getBeerByStyle("Pale Ale")
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(7)
    void testCreateBeer() {

        BeerDTO beerDTO = BeerDTO.builder()
                .price(BigDecimal.TEN)
                .beerName("Mongo Bobs")
                .beerStyle("Ipa")
                .quantityOnHand(500)
                .upc("123456")
                .build();

        beerClient.createBeer(beerDTO)
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(8)
    void testUpdateBeer() {

        final String NAME = "New Name";

        beerClient.listBeerDtos()
                .next()
                .doOnNext(beerDTO -> beerDTO.setBeerName(NAME))
                .flatMap(dto -> beerClient.updateBeer(dto))
                .subscribe(byIdDto -> {
                    System.out.println(byIdDto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(9)
    void testPatchBeer() {


        BeerDTO testDto = BeerDTO.builder()
                .beerName("Papieżowe Mocne")
                .beerStyle("Watykańskie")
                .build();

        beerClient.listBeerDtos()
                .next()
                .flatMap(dto -> beerClient.patchBeer(dto.getId(), testDto))
                .subscribe(patchedDto -> {
                    System.out.println(patchedDto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(10)
    void testDeleteBeer() {

        beerClient.listBeerDtos()
                .next()
                .flatMap(foundDto -> beerClient.deleteBeer(foundDto.getId()))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    @Order(11)
    void testDeleteBeerNotFound() {

        beerClient.listBeerDtos()
                .next()
                .flatMap(foundDto -> beerClient.deleteBeer("999"))
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }
}