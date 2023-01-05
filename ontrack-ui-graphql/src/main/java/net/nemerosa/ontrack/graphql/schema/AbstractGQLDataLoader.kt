package net.nemerosa.ontrack.graphql.schema

import org.dataloader.BatchLoaderWithContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

abstract class AbstractGQLDataLoader<K, V> : GQLDataLoader<K, V> {

    final override val batchLoader: BatchLoaderWithContext<K, V> = BatchLoaderWithContext { keys, blEnv ->
        val securityContext = blEnv.getContext<SecurityContext>()
        CompletableFuture.supplyAsync(Supplier {
            val oldSecurityContext = SecurityContextHolder.getContext()
            try {
                SecurityContextHolder.setContext(securityContext)
                loadKeys(keys)
            } finally {
                SecurityContextHolder.setContext(oldSecurityContext)
            }
        })
    }

    abstract fun loadKeys(keys: List<K>): List<V>
}