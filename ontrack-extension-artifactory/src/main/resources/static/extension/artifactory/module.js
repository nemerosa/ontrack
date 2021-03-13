angular.module('ontrack.extension.artifactory', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('artifactory-configurations', {
            url: '/extension/artifactory/configurations',
            templateUrl: 'extension/artifactory/artifactory.configurations.tpl.html',
            controller: 'ArtifactoryConfigurationsCtrl'
        });
    })
    .controller('ArtifactoryConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'Artifactory configurations';
        view.description = 'Management of the Artifactory configurations.';

        // Loading the Artifactory configurations
        function loadArtifactoryConfigurations() {
            ot.call($http.get('extension/artifactory/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'artifactory-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadArtifactoryConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "Artifactory configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadArtifactoryConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this Artifactory configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadArtifactoryConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "Artifactory configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadArtifactoryConfigurations);
        };
    })
;