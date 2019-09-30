angular.module('ontrack.extension.sonarqube', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        $stateProvider.state('sonarqube-configurations', {
            url: '/extension/sonarqube/configurations',
            templateUrl: 'extension/sonarqube/sonarqube.configurations.tpl.html',
            controller: 'SonarQubeConfigurationsCtrl'
        });
    })
    .controller('SonarQubeConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'SonarQube configurations';
        view.description = 'Management of the SonarQube configurations.';

        // Loading the configurations
        function load() {
            ot.call($http.get('extension/sonarqube/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'sonarqube-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewApiCommand(configurations._self),
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        load();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "SonarQube configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'SonarQube configuration',
                message: "Do you really want to delete this SonarQube configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(load);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "SonarQube configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })
;