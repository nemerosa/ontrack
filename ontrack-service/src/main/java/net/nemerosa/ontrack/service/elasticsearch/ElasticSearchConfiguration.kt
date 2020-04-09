package net.nemerosa.ontrack.service.elasticsearch

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchConfiguration(
        val client: RestHighLevelClient
) {

    @Bean
    fun elasticsearchRestHealthIndicator(): ElasticsearchRestHealthIndicator =
            ElasticsearchRestHealthIndicator(client.lowLevelClient)

}