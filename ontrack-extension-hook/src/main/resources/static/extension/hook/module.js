angular.module('ontrack.extension.hook', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('hook-records', {
            url: '/extension/hook/records',
            templateUrl: 'extension/hook/records.tpl.html',
            controller: 'HookRecordsCtrl'
        });
    })
    .controller('HookRecordsCtrl', function ($scope, $http, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "Hook messages";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        $scope.filter = {
            id: undefined,
            hook: undefined,
            state: undefined,
            text: undefined
        };

        const queryInfo = `
            query HookRecordsInfo {
                hookRecordFilterInfo {
                    hooks
                    states
                }
            }
        `;

        let offset = 0;
        const initialSize = 20;
        let size = initialSize;

        const query = `
            query HookRecords(
                $id: String,
                $hook: String,
                $state: HookRecordState,
                $text: String,
                $offset: Int!,
                $size: Int!,
            ) {
                hookRecordings(
                    filter: {
                        id: $id,
                        hook: $hook,
                        state: $state,
                        text: $text,
                    },
                    offset: $offset,
                    size: $size,
                ) {
                    pageInfo {
                        nextPage {
                            offset
                            size
                        }
                    }
                    pageItems {
                        id
                        hook
                        state
                        request {
                            body
                            parameters {
                                name
                                value
                            }
                        }
                        startTime
                        endTime
                        message
                        exception
                        response {
                            type
                            infoLink {
                                feature
                                id
                                data
                            }
                        }
                    }
                }
            }
        `;

        const loadRecordsInfo = () => {
            return otGraphqlService.pageGraphQLCall(queryInfo).then(data => {
                $scope.hooks = data.hookRecordFilterInfo.hooks;
                $scope.states = data.hookRecordFilterInfo.states;
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
                hook: $scope.filter.hook ? $scope.filter.hook : null,
                state: $scope.filter.state ? $scope.filter.state : null,
                text: $scope.filter.text ? $scope.filter.text : null,
                offset: offset,
                size: size,
            };
            otGraphqlService.pageGraphQLCall(query, variables).then(data => {
                if (reset) {
                    $scope.messages = data.hookRecordings.pageItems;
                } else {
                    $scope.messages = $scope.messages.concat(data.hookRecordings.pageItems);
                }
                $scope.pageInfo = data.hookRecordings.pageInfo;
            }).finally(() => {
                $scope.loading = false;
            });
        };

        loadRecordsInfo().then(() => loadRecords(true));

        $scope.onClear = () => {
            $scope.filter.id = undefined;
            $scope.filter.hook = undefined;
            $scope.filter.state = undefined;
            $scope.filter.text = undefined;
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
    })
    .directive('otHookInfoLink', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/hook/directive.hookInfoLink.tpl.html',
            scope: {
                infoLink: '='
            },
            controller: function ($scope) {
                $scope.getTemplatePath = (infoLink) =>
                    `extension/${infoLink.feature}/hookInfoLink/${infoLink.id}.tpl.html`;
            }
        };
    })
;