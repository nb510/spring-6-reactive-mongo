package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.web.fn.BeerRouterConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.List;

import static guru.springframework.reactivemongo.service.BeerServiceImplTest.getTestBeer;
import static guru.springframework.reactivemongo.web.fn.BeerRouterConfig.BEER_PATH;
import static guru.springframework.reactivemongo.web.fn.BeerRouterConfig.BEER_PATH_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerEndpointsTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BeerMapper beerMapper;

    @Test
    void testGetBeerByStyle() {
        BeerDTO beerDto = beerMapper.beerToBeerDto(getTestBeer());
        beerDto.setBeerStyle("Test");

        for (int i = 0; i < 4; i++) {
            webTestClient.post().uri(BEER_PATH)
                    .body(Mono.just(beerDto), BeerDTO.class)
                    .exchange();
        }

        webTestClient.get().uri(UriComponentsBuilder.fromPath(BEER_PATH)
                        .queryParam("style", beerDto.getBeerStyle()).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.size()").value(equalTo(4));
    }

    @Test
    void testBeerNotFound() {
        webTestClient.get().uri(BEER_PATH_ID, 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.get().uri(BEER_PATH_ID, 100)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        webTestClient.put().uri(BEER_PATH_ID, 100)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateBeer() {
        webTestClient.put().uri(BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateBeer() {
        webTestClient.post().uri(BEER_PATH)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location")
                .returnResult(BeerDTO.class);
    }

    @Test
    void testGetBeerById() {
        webTestClient.get().uri(BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDTO.class);
    }

    @Test
    void testListBeers() {
        webTestClient.get().uri(BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }


}
