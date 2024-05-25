# Measuring and metrics

## Measuring execution times

Inject the `MeterRegistry` in your class.

Use the `MeterRegistry.time` function from the `MetricsExtensions` Kotlin extensions:

```kotlin
val result = meterRegistry.time(
    "metric_name",
    "tag1" to "value1",
    "tag2" to "value2",
) {
    // .. code returning a result
}
```

## Measuring times and result types

If measuring the time is not enough and you also want to count the errors and successes, you can use the `measure` function.

For example, for the SonarQube measurements:

```kotlin
val measures = meterRegistry.measure(
    started = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_STARTED_COUNT,
    success = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_SUCCESS_COUNT,
    error = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_ERROR_COUNT,
    time = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_TIME,
    tags = metricTags // Map of tags
) {
    // ... code returning a result
}
```
