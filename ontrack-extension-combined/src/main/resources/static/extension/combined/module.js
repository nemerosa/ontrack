angular.module('ontrack.extension.combined', [
    'ui.router',
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Combined issue service configurations
        $stateProvider.state('combined-issue-service-configurations', {
            url: '/extension/combined/configurations',
            templateUrl: 'extension/combined/combined.configurations.tpl.html',
            controller: 'CombinedConfigurationsCtrl'
        });
    })
    .controller('CombinedConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        var view = ot.view();
        view.title = 'Combined issue services configurations';
        view.description = 'Management of the combined issue services configurations.';

        // Loading the configurations
        function loadConfigurations() {
            ot.call($http.get('extension/issue/combined/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "JIRA configuration",
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this JIRA configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "JIRA configuration",
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadConfigurations);
        };
    })
    .directive('otExtensionJiraIssueLink', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/jira/directive.jiraIssueLink.tpl.html',
            scope: {
                issue: '='
            }
        };
    })
;