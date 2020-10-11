package com.hari.circuitbreaker.resource;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HariomYadav
 * @since 11/10/20
 */
@RestController
@RequestMapping("/v1/cb")
@Slf4j
public class CircuitBreakerResource {

//    @Value(value = "${SAMPLE_APP_URI}")
    private String SAMPLE_APP_URI = "http://localhost:8082/v1/api";

    RestTemplate restTemplate;

    public CircuitBreakerResource(RestTemplateBuilder restTemplateBuilder) {
        System.err.println("CircuitBreakerResource.CircuitBreakerResource");
        UriTemplateHandler uriTemplateHandler = new RootUriTemplateHandler(SAMPLE_APP_URI);
        final RestTemplate restTemplate = restTemplateBuilder.uriTemplateHandler(uriTemplateHandler).build();
        this.restTemplate = restTemplate;
    }


    @CircuitBreaker(name = "service1", fallbackMethod = "getDataFromSampleServiceFallback")//wrap with CB
    @RequestMapping(method = RequestMethod.GET)
    public String getDataFromSampleService() {
        System.err.println("CircuitBreakerResource.getDataFromSampleService");
        final String response = restTemplate.getForObject("/", String.class);
        System.out.println("response = " + response);
        return response;
    }

    public String getDataFromSampleServiceFallback(Throwable throwable) {
        System.err.println("CircuitBreakerResource.getDataFromSampleServiceFallback");
        System.out.println(throwable.getMessage().toString());
        return "CircuitBreakerResource.getDataFromSampleServiceFallback";
    }

    @RequestMapping(method = RequestMethod.POST)//not wrap with CB
    public String postDataToSampleService() {
        System.err.println("CircuitBreakerResource.postDataToSampleService");
        final String response = restTemplate.postForObject("/", null, String.class);
        System.out.println("response = " + response);
        return response;
    }

}
