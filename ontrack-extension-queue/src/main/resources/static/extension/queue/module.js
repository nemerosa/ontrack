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
    .controller('QueueRecordsCtrl', function ($scope, $http, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "Queue messages";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        $scope.filter = {
            id: undefined,
            processor: undefined,
            state: undefined
        };

        const queryInfo = `
            query QueueRecordsInfo {
                queueRecordFilterInfo {
                    processors
                    states
                }
            }
        `;

        const query = `
            query QueueRecords(
                $id: String,
                $processor: String,
                $state: QueueRecordState,
            ) {
                queueRecords(
                    id: $id,
                    processor: $processor,
                    state: $state,
                ) {
                    pageItems {
                        state
                        queuePayload {
                            id
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

        const loadRecords = () => {
            $scope.loading = true;
            const variables = {
                id: $scope.filter.id ? $scope.filter.id : null,
                processor: $scope.filter.processor ? $scope.filter.processor : null,
                state: $scope.filter.state ? $scope.filter.state : null,
            };
            otGraphqlService.pageGraphQLCall(query, variables).then(data => {
                $scope.messages = data.queueRecords.pageItems;
            }).finally(() => {
                $scope.loading = false;
            });
        };

        loadRecordsInfo().then(loadRecords);

        $scope.onClear = () => {
            $scope.filter.id = undefined;
            $scope.filter.processor = undefined;
            $scope.filter.state = undefined;
            loadRecords();
        };

        $scope.onFilter = () => {
            loadRecords();
        };

        $scope.toggleMessage = (message) => {
            message.details = !message.details;
        };
    })
;