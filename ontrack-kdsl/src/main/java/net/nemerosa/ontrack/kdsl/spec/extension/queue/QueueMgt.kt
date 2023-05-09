package net.nemerosa.ontrack.kdsl.spec.extension.queue

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.QueueGetRecordQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class QueueMgt(connector: Connector) : Connected(connector) {

    val mock: MockQueueMgt by lazy {
        MockQueueMgt(connector)
    }

    fun findQueueRecordByID(queueID: String): QueueRecord? =
            graphqlConnector.query(
                    QueueGetRecordQuery.builder()
                            .id(queueID)
                            .build()
            )?.queueRecordings()?.pageItems()?.firstOrNull()
                    ?.fragments()?.queueRecordData()?.run {
                        QueueRecord(
                                state = QueueRecordState.valueOf(state().name),
                                queuePayload = queuePayload().run {
                                    QueuePayload(
                                            id = id(),
                                            processor = processor(),
                                            body = body(),
                                    )
                                },
                                startTime = startTime(),
                                endTime = endTime(),
                                routingKey = routingKey(),
                                queueName = queueName(),
                                actualPayload = actualPayload(),
                                exception = exception(),
                                history = history().map {
                                    QueueRecordHistory(
                                            state = QueueRecordState.valueOf(it.state().name),
                                            time = it.time(),
                                    )
                                }
                        )
                    }

}