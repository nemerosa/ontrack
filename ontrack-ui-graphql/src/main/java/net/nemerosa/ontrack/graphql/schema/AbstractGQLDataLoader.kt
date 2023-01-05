package net.nemerosa.ontrack.graphql.schema

import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

abstract class AbstractGQLDataLoader<K, V> : GQLDataLoader<K, V> {

    final override val batchLoader: BatchLoader<K, V> = BatchLoader { keys ->
        CompletableFuture.supplyAsync(Supplier {
            loadKeys(keys)
        })
    }

    abstract fun loadKeys(keys: List<K>): List<V>
}