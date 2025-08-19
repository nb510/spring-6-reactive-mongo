package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static guru.springframework.reactivemongo.service.BeerServiceImplTest.getTestBeer;
import static guru.springframework.reactivemongo.web.fn.BeerRouterConfig.BEER_PATH;
import static org.hamcrest.Matchers.equalTo;

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

}
