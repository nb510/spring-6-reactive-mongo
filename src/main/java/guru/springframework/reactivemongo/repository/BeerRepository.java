package guru.springframework.reactivemongo.repository;

import guru.springframework.reactivemongo.domain.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {

    Flux<Beer> findByBeerStyle(String beerStyle);
}
