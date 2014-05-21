angular.module('ontrack.extension.jenkins', [
    'ui.router',
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Jenkins settings
        $stateProvider.state('jenkins-settings', {
            url: '/extension/jenkins/settings',
            templateUrl: 'app/extension/jenkins/jenkins.settings.tpl.html',
            controller: 'JenkinsSettingsCtrl'
        });
    })
    .controller('JenkinsSettingsCtrl', function ($scope, $http, ot, otFormService) {
        var view = ot.view();
        view.title = 'Jenkins settings';
        view.description = 'Management of the Jenkins settings and configurations.';

        // Loading the Jenkins settings
        function loadJenkinsSettings() {
            ot.call($http.get('extension/jenkins/settings')).then(function (settings) {
                $scope.settings = settings;
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

        loadJenkinsSettings();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.settings.createConfiguration.href,
                title: "Jenkins configuration",
                submit: function (data) {
                    return ot.call($http.post($scope.settings.createConfiguration.href, data));
                }
            });
        };
    })
;