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

        const query = `
            query QueueRecords {
                queueRecords {
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

        const loadRecords = () => {
            $scope.loading = true;
            otGraphqlService.pageGraphQLCall(query, {}).then(data => {
                $scope.messages = data.queueRecords.pageItems;
            }).finally(() => {
                $scope.loading = false;
            });
        };

        loadRecords();
    })
;