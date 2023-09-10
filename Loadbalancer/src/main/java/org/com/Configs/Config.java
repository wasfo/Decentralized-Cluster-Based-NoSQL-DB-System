package org.com.Configs;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {



    @Bean("writeTemplate")
    @LoadBalanced
    public RestTemplate writeQueriesTemplate() {
        return new RestTemplate();
    }
    @Bean("registerTemplate")
    @LoadBalanced
    public RestTemplate registerTemplate() {
        return new RestTemplate();
    }

    @Bean("loginTemplate")
    @LoadBalanced
    public RestTemplate loginTemplate() {
        return new RestTemplate();
    }


}
