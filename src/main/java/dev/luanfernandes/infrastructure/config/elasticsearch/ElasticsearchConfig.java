package dev.luanfernandes.infrastructure.config.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "dev.luanfernandes.adapter.out.search.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch.connection-timeout}")
    private String connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout}")
    private String socketTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUris.replace("http://", "").replace("https://", ""))
                .withBasicAuth(username, password)
                .withConnectTimeout(java.time.Duration.parse("PT" + connectionTimeout.toUpperCase()))
                .withSocketTimeout(java.time.Duration.parse("PT" + socketTimeout.toUpperCase()))
                .build();
    }
}
