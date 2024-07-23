package com.example.clientms;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implemented design patterns:
 *  service registry
 *  service discovery
 *  client-side load balancing
 *  circuit breaker
 */
@RestController
public class ClientResource {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientResource.class);
    
    @Autowired
    private WebClient webClient;

    @CircuitBreaker(name = "bookmsclient", fallbackMethod = "bookmsfallback")
    @GetMapping("/client/books")
    public Mono getBooksFromBookms() {
        LOGGER.info("client getBooksFromBookms is called");
        return webClient.get().uri("/books").retrieve().bodyToMono(Object.class);
    }

    /**
     * This is called when circuit is open
     * @param ex
     * @return
     */
    private Mono bookmsfallback(CallNotPermittedException ex) {
        LOGGER.info("CallNotPermittedException-Fallback is invoked: ", ex);
        // you can call Database, you can call another microservice
        return Mono.just("CallNotPermittedException happened");
    }

    /**
     * This is called when webClient call fails
     * @param ex
     * @return
     */
    private Mono bookmsfallback(Exception ex) {
        LOGGER.info("Exception-Fallback is invoked: ", ex);
        // you can call Database, you can call another microservice
        return Mono.just("Exception happened");
    }

}
