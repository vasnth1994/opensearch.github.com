package com.example.opensearchdemo.config;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.RestClient;
import org.apache.http.HttpHost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {
    @Bean
    public RestHighLevelClient openSearchClient() {
        return new RestHighLevelClient(
            RestClient.builder(
            new HttpHost("ip172-18-0-15-d1agmagl2o9000d8n9cg-9200.direct.labs.play-with-docker.com", 9200, "http")
            )
        );
    }
}
