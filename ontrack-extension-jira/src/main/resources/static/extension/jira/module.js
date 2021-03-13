angular.module('ontrack.extension.jira', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Jira configurations
        $stateProvider.state('jira-configurations', {
            url: '/extension/jira/configurations',
            templateUrl: 'extension/jira/jira.configurations.tpl.html',
            controller: 'JiraConfigurationsCtrl'
        });
    })
    .controller('JiraConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'JIRA configurations';
        view.description = 'Management of the JIRA configurations.';

        // Loading the Jira configurations
        function loadJiraConfigurations() {
            ot.call($http.get('extension/jira/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'jira-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadJiraConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "JIRA configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadJiraConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this JIRA configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadJiraConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "JIRA configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadJiraConfigurations);
        };
    })
;