# List of metrics

This page lists all the metrics emitted by Yontrack.

## Notifications

| Metric                                                                | Tags                          | Description                                                                                     |
|-----------------------------------------------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------|
| `ontrack_extension_notifications_event_listening_received`            | `event`                       | Count of events being received                                                                  |
| `ontrack_extension_notifications_event_listening_queued`              | `event`                       | Count of events being actually queued                                                           |
| `ontrack_extension_notifications_event_listening_dequeued`            | `event`                       | Count of events being removed from the queue for dispatching                                    |
| `ontrack_extension_notifications_event_listening_dequeued_error`      | None                          | Count of uncaught errors when listening to events                                               |
| `ontrack_extension_notifications_event_listening`                     | `event`                       | Count of events whose dispatching starts                                                        |
| `ontrack_extension_notifications_event_dispatching_queued`            | `event`, `channel`, `routing` | Count of events whose dispatching starts (pushed into the dispatching queue)                    |
| `ontrack_extension_notifications_event_dispatching_dequeued`          | `event`, `channel`, `routing` | Count of events whose dispatching starts (pulled from the dispatching queue)                    |
| `ontrack_extension_notifications_event_dispatching_result`            | `event`, `channel`, `result`  | Count of events whose dispatching is finished                                                   |
| `ontrack_extension_notifications_event_processing_started`            | `event`, `channel`            | Count of events whose processing is started                                                     |
| `ontrack_extension_notifications_event_processing_channel_started`    | `event`, `channel`            | Count of events whose processing is started on an actual channel                                |
| `ontrack_extension_notifications_event_processing_channel_unknown`    | `event`, `channel`            | Count of events whose processing is stopped because the channel is unknown                      |
| `ontrack_extension_notifications_event_processing_channel_invalid`    | `event`, `channel`            | Count of events whose processing is stopped because the channel or its configuration is invalid |
| `ontrack_extension_notifications_event_processing_channel_publishing` | `event`, `channel`            | Count of events whose publication into a channel has started                                    |
| `ontrack_extension_notifications_event_processing_channel_result`     | `event`, `channel`, `result`  | Count of events whose publication into a channel has finished                                   |
| `ontrack_extension_notifications_event_processing_channel_error`      | `event`, `channel`            | Count of events whose publication into a channel has failed                                     |

