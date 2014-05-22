angular.module('ontrack.extension.jenkins', [
    'ui.router',
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Jenkins configurations
        $stateProvider.state('jenkins-configurations', {
            url: '/extension/jenkins/configurations',
            templateUrl: 'app/extension/jenkins/jenkins.configurations.tpl.html',
            controller: 'JenkinsConfigurationsCtrl'
        });
    })
    .controller('JenkinsConfigurationsCtrl', function ($scope, $http, ot, otFormService) {
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
                uri: $scope.configurations.createConfiguration.href,
                title: "Jenkins configuration",
                submit: function (data) {
                    return ot.call($http.post($scope.configurations.createConfiguration.href, data));
                }
            }).then(loadJenkinsConfigurations);
        };
    })
;