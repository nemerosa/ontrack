package net.nemerosa.ontrack.extension.hook.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.model.metrics.increment

private fun MeterRegistry.hookMetric(metric: String, hook: String) =
    increment(metric, "hook" to hook)

fun MeterRegistry.hookUndefined(hook: String) =
    hookMetric(HookMetrics.undefined, hook)

fun MeterRegistry.hookDisabled(hook: String) =
    hookMetric(HookMetrics.disabled, hook)

fun MeterRegistry.hookAccessDenied(hook: String) =
    hookMetric(HookMetrics.denied, hook)

fun MeterRegistry.hookSuccess(hook: String) =
    hookMetric(HookMetrics.success, hook)

fun MeterRegistry.hookError(hook: String) =
    hookMetric(HookMetrics.error, hook)
