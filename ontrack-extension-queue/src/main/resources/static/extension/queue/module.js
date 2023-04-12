angular.module('ontrack.extension.queue', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('queue-records', {
            url: '/extension/queue/records',
            templateUrl: 'extension/queue/records.tpl.html',
            controller: 'QueueRecordsCtrl'
        });
    })
    .controller('QueueRecordsCtrl', function ($scope, $http, ot, otAlertService, otGraphqlService) {
        const view = ot.view();
        view.title = "Queue messages";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        $scope.filter = {
            id: undefined,
            processor: undefined,
            state: undefined,
            text: undefined,
            routing: undefined,
            queue: undefined
        };

        const queryInfo = `
            query QueueRecordsInfo {
                queueRecordFilterInfo {
                    processors
                    states
                }
            }
        `;

        let offset = 0;
        const initialSize = 20;
        let size = initialSize;

        const query = `
            query QueueRecords(
                $id: String,
                $processor: String,
                $state: QueueRecordState,
                $text: String,
                $routing: String,
                $queue: String,
                $offset: Int!,
                $size: Int!,
            ) {
                queueRecordings(filter: {
                    id: $id,
                    processor: $processor,
                    state: $state,
                    routing: $routing,
                    queue: $queue,
                    text: $text,
                    offset: $offset,
                    size: $size,
                }) {
                    pageInfo {
                        nextPage {
                            offset
                            size
                        }
                    }
                    pageItems {
                        id
                        state
                        queuePayload {
                            processor
                            body
                        }
                        startTime
                        endTime
                        routingKey
                        queueName
                        actualPayload
                        exception
                        history {
                            state
                            time
                        }
                    }
                }
            }
        `;

        const loadRecordsInfo = () => {
            return otGraphqlService.pageGraphQLCall(queryInfo).then(data => {
                 $scope.processors = data.queueRecordFilterInfo.processors;
                 $scope.states = data.queueRecordFilterInfo.states;
            });
        };

        const loadRecords = (reset) => {
            $scope.loading = true;

            if (reset) {
                offset = 0;
                size = initialSize;
            }

            const variables = {
                id: $scope.filter.id ? $scope.filter.id : null,
                processor: $scope.filter.processor ? $scope.filter.processor : null,
                state: $scope.filter.state ? $scope.filter.state : null,
                text: $scope.filter.text ? $scope.filter.text : null,
                routing: $scope.filter.routing ? $scope.filter.routing : null,
                queue: $scope.filter.queue ? $scope.filter.queue : null,
                offset: offset,
                size: size,
            };
            otGraphqlService.pageGraphQLCall(query, variables).then(data => {
                if (reset) {
                    $scope.messages = data.queueRecordingsRecordings.pageItems;
                } else {
                    $scope.messages = $scope.messages.concat(data.queueRecordingsRecordings.pageItems);
                }
                $scope.pageInfo = data.queueRecordingsRecordings.pageInfo;
            }).finally(() => {
                $scope.loading = false;
            });
        };

        loadRecordsInfo().then(() => loadRecords(true));

        $scope.onClear = () => {
            $scope.filter.id = undefined;
            $scope.filter.processor = undefined;
            $scope.filter.state = undefined;
            $scope.filter.text = undefined;
            $scope.filter.routing = undefined;
            $scope.filter.queue = undefined;
            loadRecords(true);
        };

        $scope.onFilter = () => {
            loadRecords(true);
        };

        $scope.toggleMessage = (message) => {
            message.details = !message.details;
        };

        $scope.loadNextPage = () => {
            if ($scope.pageInfo.nextPage) {
                offset = $scope.pageInfo.nextPage.offset;
                size = $scope.pageInfo.nextPage.size;
                loadRecords(false);
            }
        };

        $scope.onPurge = () => {
            otAlertService.confirm({
                title: "Purging the queue records",
                message: "Do you really want to remove all the records of the messages?"
            }).then(() => {
                return otGraphqlService.pageGraphQLCallWithPayloadErrors(`
                    mutation {
                        purgeQueueRecordingsRecordings {
                            errors {
                                message
                            }
                        }
                    }
                `, {}, 'purgeQueueRecordingsRecordings');
            }).then(() => {
                loadRecords(true);
            });
        };
    })
;