package com.hari.circuitbreaker.resource;

import java.time.Duration;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HariomYadav
 * @since 11/10/20
 */
@RestController
@RequestMapping("/v2/cb")
@Slf4j
public class CircuitBreakerResource2 {

    private String SAMPLE_APP_URI = "http://localhost:8082/v1/api";

    RestTemplate restTemplate;

    public CircuitBreakerResource2(RestTemplateBuilder restTemplateBuilder) {
        System.err.println("CircuitBreakerResource2.CircuitBreakerResource");
        UriTemplateHandler uriTemplateHandler = new RootUriTemplateHandler(SAMPLE_APP_URI);
        final RestTemplate restTemplate = restTemplateBuilder.uriTemplateHandler(uriTemplateHandler).build();

        this.restTemplate = restTemplate;
    }

    //working
//    @RequestMapping(method = RequestMethod.GET)
//    public String getDataFromSampleService(@DefaultValue("old value") String old) {
//        System.err.println("CircuitBreakerResource2.getDataFromSampleService");
//        //2. get cb registry obj
//        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
//        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("service3");
//
////        CheckedFunction0<String> decoratedSupplier = CircuitBreaker
////                .decorateCheckedSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");
//
//        CheckedFunction0<String> decoratedSupplier2 = CircuitBreaker
//                .decorateCheckedSupplier(circuitBreaker, () -> restTemplate.getForObject("/", String.class));
//        final Try<String> result = Try.of(decoratedSupplier2).map(value -> value + " world ");
//        System.out.println("result.isSuccess() = " + result.isSuccess());
//        System.out.println("result.get() = " + result.get());
//        if (result.isSuccess()) {
//            return result.get();
//        } else {
//            return "failed";
//        }
//    }


    @RequestMapping(method = RequestMethod.GET)
    public String getDataFromSampleService(@DefaultValue("old value") String old) {
        System.err.println("CircuitBreakerResource2.getDataFromSampleService");
        //1. CB config
        final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom().failureRateThreshold(50).waitDurationInOpenState(Duration.ofMillis(10000))
                .permittedNumberOfCallsInHalfOpenState(3).slidingWindowSize(5).build();
        //2. cb registry obj
        final CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        //3. cb obj
        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("service3");
        //4. supplier obj
        final Supplier<String> tSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, () -> restTemplate.getForObject("/", String.class));
        //5. M1. result obj using Try - with fallback logic -working
//        final Try<String> recover = Try.ofSupplier(tSupplier).recover(t -> {
//            //.. operation when service call failed
//            System.out.println("t.getMessage().toString() = " + t.getMessage().toString());
//            return "failed";
//        });

        //M2 - without fallback logic - only decorate with circuit breaker - working
        final Try<String> recover = Try.ofSupplier(tSupplier);

        //6. return result - success or failure
        System.out.println("result.isSuccess() = " + recover.isSuccess());
        System.out.println("result.get() = " + recover.get());
        return recover.get();
    }

    @RequestMapping(method = RequestMethod.POST)//not wrap with CB
    public String postDataToSampleService() {
        System.err.println("CircuitBreakerResource.postDataToSampleService");
        final String response = restTemplate.postForObject("/", null, String.class);
        System.out.println("response = " + response);
        return response;
    }

}
