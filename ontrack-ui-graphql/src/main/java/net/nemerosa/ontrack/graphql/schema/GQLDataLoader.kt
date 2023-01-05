package net.nemerosa.ontrack.graphql.schema

import org.dataloader.BatchLoader
import org.dataloader.BatchLoaderWithContext

/**
 * Registration of a data loader.
 *
 * @param <K> type parameter indicating the type of the data load keys
 * @param <V> type parameter indicating the type of the data that is returned
 */
interface GQLDataLoader<K, V> {

    /**
     * Key for the data loader
     */
    val key: String

    /**
     * Associated data loader
     */
    val batchLoader: BatchLoaderWithContext<K, V>

}