angular.module('ontrack.extension.jenkins', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Jenkins configurations
        $stateProvider.state('jenkins-configurations', {
            url: '/extension/jenkins/configurations',
            templateUrl: 'extension/jenkins/jenkins.configurations.tpl.html',
            controller: 'JenkinsConfigurationsCtrl'
        });
    })
    .controller('JenkinsConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'Jenkins configurations';
        view.description = 'Management of the Jenkins configurations.';

        // Loading the Jenkins configurations
        function loadJenkinsConfigurations() {
            ot.call($http.get('extension/jenkins/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'jenkins-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadJenkinsConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "Jenkins configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadJenkinsConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this Jenkins configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadJenkinsConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "Jenkins configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadJenkinsConfigurations);
        };
    })
;