package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {

    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDto);

    Mono<BeerDTO> getById(String beerId);

    Flux<BeerDTO> listBeers();

    Flux<BeerDTO> getBeerByStyle(String beerStyle);
}
