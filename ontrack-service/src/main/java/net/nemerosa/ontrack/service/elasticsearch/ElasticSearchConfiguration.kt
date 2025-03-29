package net.nemerosa.ontrack.service.elasticsearch

import org.elasticsearch.client.RestClient
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestClientHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchConfiguration(
    val restClient: RestClient,
) {

    @Bean
    fun elasticsearchRestHealthIndicator(): ElasticsearchRestClientHealthIndicator =
        ElasticsearchRestClientHealthIndicator(restClient)

}