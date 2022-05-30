angular.module('ontrack.extension.github', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.configuration',
    'ot.service.form',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('github-ingestion-hook-payloads', {
            url: '/extension/github/ingestion-hook-payloads',
            templateUrl: 'extension/github/ingestion-hook-payloads.tpl.html',
            controller: 'GitHubIngestionHookPayloadsCtrl'
        });
    })
    .controller('GitHubIngestionHookPayloadsCtrl', function ($scope, ot, otGraphqlService, otTaskService) {
        const view = ot.view();
        view.title = 'GitHub Ingestion Hook Payloads';
        view.description = 'List of payloads received and processed by the GitHub Ingestion Hook.';
        view.commands = [];
        view.breadcrumbs = ot.homeBreadcrumbs();

        const query = `
            query GetPayloads(
                $offset: Int!, 
                $size: Int!,
                $statuses: [IngestionHookPayloadStatus!],
                $outcome: IngestionEventProcessingResult,
                $gitHubDelivery: String,
                $gitHubEvent: String,
                $source: String,
                $repository: String,
                $owner: String,
                $routing: String,
                $queue: String,
            ) {
                gitHubIngestionHookPayloadStatuses
                gitHubIngestionEventProcessingResults
                gitHubIngestionHookPayloads(
                    offset: $offset,
                    size: $size,
                    statuses: $statuses,
                    outcome: $outcome,
                    gitHubDelivery: $gitHubDelivery,
                    gitHubEvent: $gitHubEvent,
                    source: $source,
                    repository: $repository,
                    owner: $owner,
                    routing: $routing,
                    queue: $queue,
                ) {
                    pageInfo {
                        totalSize
                        previousPage {
                            offset
                            size
                        }
                        nextPage {
                            offset
                            size
                        }
                    }
                    pageItems {
                        uuid
                        timestamp
                        gitHubEvent
                        gitHubDelivery
                        status
                        outcome
                        outcomeDetails
                        completion
                        routing
                        queue
                        repository {
                            name
                            htmlUrl
                            owner {
                                login
                            }
                        }
                        source
                    }
                }
            }
        `;

        const detailsQuery = `
            query GetPayloadDetail(
                $uuid: String!
            ) {
                gitHubIngestionHookPayloads(
                    uuid: $uuid,
                ) {
                    pageItems {
                        message
                        payload
                    }
                }
            }
        `;

        const defaultSize = 20;

        const variables = {
            offset: 0,
            size: defaultSize,
            statuses: null,
            outcome: null,
            source: null,
            repository: null,
            owner: null,
            routing: null,
            queue: null,
        };

        $scope.filter = {
            statuses: {},
            gitHubDelivery: '',
            outcome: '',
            gitHubEvent: '',
            source: '',
            repository: '',
            owner: '',
            routing: '',
            queue: '',
        };

        $scope.loadingPayloads = true;

        const loadPayloads = () => {
            $scope.loadingPayloads = true;
            otGraphqlService.pageGraphQLCall(query, variables).then(data => {
                $scope.statuses = data.gitHubIngestionHookPayloadStatuses;
                $scope.outcomes = data.gitHubIngestionEventProcessingResults;
                $scope.payloads = data.gitHubIngestionHookPayloads.pageItems;
                $scope.pageInfo = data.gitHubIngestionHookPayloads.pageInfo;
            }).finally(() => {
                $scope.loadingPayloads = false;
            });
        };

        loadPayloads();

        $scope.submitFilter = () => {
            variables.offset = 0;
            variables.size = defaultSize;
            if ($scope.filter.outcome) {
                variables.outcome = $scope.filter.outcome;
            } else {
                variables.outcome = null;
            }
            if ($scope.filter.gitHubDelivery) {
                variables.gitHubDelivery = $scope.filter.gitHubDelivery;
            } else {
                variables.gitHubDelivery = null;
            }
            if ($scope.filter.gitHubEvent) {
                variables.gitHubEvent = $scope.filter.gitHubEvent;
            } else {
                variables.gitHubEvent = null;
            }
            if ($scope.filter.source) {
                variables.source = $scope.filter.source;
            } else {
                variables.source = null;
            }
            if ($scope.filter.repository) {
                variables.repository = $scope.filter.repository;
            } else {
                variables.repository = null;
            }
            if ($scope.filter.owner) {
                variables.owner = $scope.filter.owner;
            } else {
                variables.owner = null;
            }
            if ($scope.filter.routing) {
                variables.routing = $scope.filter.routing;
            } else {
                variables.routing = null;
            }
            if ($scope.filter.queue) {
                variables.queue = $scope.filter.queue;
            } else {
                variables.queue = null;
            }
            loadPayloads();
        };

        $scope.resetFilter = () => {
            $scope.filter.statuses = {};
            $scope.filter.outcome = '';
            $scope.filter.gitHubDelivery = '';
            $scope.filter.gitHubEvent = '';
            $scope.filter.source = '';
            $scope.filter.repository = '';
            $scope.filter.owner = '';
            $scope.filter.routing = '';
            $scope.filter.queue = '';
            $scope.submitFilter();
        };

        $scope.onStatusSelection = () => {
            variables.offset = 0;
            variables.size = defaultSize;
            variables.statuses = [];
            $scope.statuses.forEach((status) => {
                if ($scope.filter.statuses[status] === true) {
                    variables.statuses.push(status);
                }
            });
            if (variables.statuses.length === 0) {
                variables.statuses = null;
            }
            loadPayloads();
        };

        const autoReloadKey = 'github-ingestion-hook-payloads-auto-reload';
        const storedAutoReload = localStorage.getItem(autoReloadKey);
        if (storedAutoReload !== null) {
            $scope.autoReload = storedAutoReload;
        } else {
            $scope.autoReload = false;
        }

        const interval = 10 * 1000; // 10 seconds
        const taskName = 'GitHub Ingestion Hook Payloads';
        const registerReload = () => {
            otTaskService.register(taskName, loadPayloads, interval);
        };

        if ($scope.autoReload) {
            registerReload();
        }

        $scope.toggleAutoReload = () => {
            $scope.autoReload = !$scope.autoReload;
            if ($scope.autoReload) {
                localStorage.setItem(autoReloadKey, $scope.autoReload);
                registerReload();
            } else {
                localStorage.removeItem(autoReloadKey);
                otTaskService.stop(taskName);
            }
        };

        $scope.topPayloads = () => {
            variables.offset = 0;
            variables.size = defaultSize;
            loadPayloads();
        };

        $scope.newerPayloads = () => {
            if ($scope.pageInfo.previousPage) {
                variables.offset = $scope.pageInfo.previousPage.offset;
                variables.size = $scope.pageInfo.previousPage.size;
                loadPayloads();
            }
        };

        $scope.olderPayloads = () => {
            if ($scope.pageInfo.nextPage) {
                variables.offset = $scope.pageInfo.nextPage.offset;
                variables.size = $scope.pageInfo.nextPage.size;
                loadPayloads();
            }
        };

        $scope.toggleDetails = (payload) => {
            if (!payload.loadingDetails) {
                payload.showDetails = !payload.showDetails;
                if (payload.showDetails && !payload.details) {
                    // Loading the details
                    payload.loadingDetails = true;
                    otGraphqlService.pageGraphQLCall(detailsQuery, {uuid: payload.uuid}).then(data => {
                        payload.details = data.gitHubIngestionHookPayloads.pageItems[0];
                        payload.details.payloadJson = JSON.stringify(payload.details.payload, null, 3);
                    }).finally(() => {
                        payload.loadingDetails = false;
                    });
                }
            }
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('github-configurations', {
            url: '/extension/github/configurations',
            templateUrl: 'extension/github/github.configurations.tpl.html',
            controller: 'GitHubConfigurationsCtrl'
        });
    })
    .controller('GitHubConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService, otGraphqlService) {
        const view = ot.view();
        view.title = 'GitHub configurations';
        view.description = 'Management of the GitHub configurations.';
        view.commands = [];

        // Query to get the list of configurations
        const query = `
            {
                gitHubConfigurations {
                    name
                    url
                    authenticationType
                    rateLimits {
                        core {
                            limit
                            used
                        }
                        graphql {
                            limit
                            used
                        }
                    }
                    user
                    appId
                    appInstallationAccountName
                    appToken {
                        valid
                        createdAt
                        validUntil
                    }
                    links {
                        _update
                        _delete
                    }
                }
            }
        `;

        // Loading the configurations
        $scope.loadingConfigurations = true;
        function load() {
            $scope.loadingConfigurations = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.configurations = data.gitHubConfigurations;
                view.commands = [
                    {
                        id: 'github-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            }).finally(() => {
                $scope.loadingConfigurations = false;
            });
        }

        load();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: '/extension/github/configurations/create',
                title: "GitHub configuration",
                buttons: [ otConfigurationService.testButton('/extension/github/configurations/test') ],
                submit: function (data) {
                    return ot.call($http.post('/extension/github/configurations/create', data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'GitHub configuration',
                message: "Do you really want to delete this GitHub configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration.links._delete)).then(load);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration.links._update,
                title: "GitHub configuration",
                buttons: [ otConfigurationService.testButton('/extension/github/configurations/test') ],
                submit: function (data) {
                    return ot.call($http.put(configuration.links._update, data));
                }
            }).then(load);
        };
    })
;