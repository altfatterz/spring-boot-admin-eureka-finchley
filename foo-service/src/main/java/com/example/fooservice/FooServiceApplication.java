package com.example.fooservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient // need to reach the bar-service, otherwise "java.lang.IllegalStateException: No instances available for bar-service"
public class FooServiceApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FooServiceApplication.class, args);
    }
}

@RestController
class FooController {

    private final BarClient barClient;

    public FooController(BarClient barClient) {
        this.barClient = barClient;
    }

    @GetMapping
    public String foo() {
        return "foo";
    }

    @GetMapping("/foobar")
    public String fooBar() {
        return "foo" + barClient.getBar();
    }

}

@Component
class BarClient {

    private final RestTemplate restTemplate;

    public BarClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getBar() {
        return restTemplate.getForObject("http://bar-service", String.class);
    }
}