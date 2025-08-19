package guru.springframework.reactivemongo.web.fn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BeerRouterConfig {

    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    @Autowired
    private BeerHandler beerHandler;

    @Bean
    public RouterFunction<ServerResponse> listBeers() {
        return route()
                .GET(BEER_PATH, accept(APPLICATION_JSON), beerHandler::listBeers)
                .GET(BEER_PATH_ID, accept(APPLICATION_JSON), beerHandler::getById)
                .POST(BEER_PATH, accept(APPLICATION_JSON), beerHandler::createBeer)
                .PUT(BEER_PATH_ID, accept(APPLICATION_JSON), beerHandler::updateBeer)
                .DELETE(BEER_PATH_ID, accept(APPLICATION_JSON), beerHandler::deleteBeer)
                .build();
    }
}
