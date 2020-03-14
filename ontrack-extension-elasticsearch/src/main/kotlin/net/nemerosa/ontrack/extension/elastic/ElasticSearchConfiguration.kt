package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestHealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchConfiguration(
        val client: RestHighLevelClient
) {

    @Bean
    fun elasticsearchRestHealthIndicator(): ElasticsearchRestHealthIndicator =
            ElasticsearchRestHealthIndicator(client.lowLevelClient)

}