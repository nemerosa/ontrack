angular.module('ontrack.extension.jenkins', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        // Jenkins settings
        $stateProvider.state('jenkins-settings', {
            url: '/extension/jenkins/settings',
            templateUrl: 'app/extension/jenkins/jenkins.settings.tpl.html',
            controller: 'JenkinsSettingsCtrl'
        });
    })
    .controller('JenkinsSettingsCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = 'Jenkins settings';
        view.description = 'Management of the Jenkins settings and configurations.';
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loading the Jenkins settings
        function loadJenkinsSettings() {
            ot.call($http.get('extension/jenkins/settings')).then(function (settings) {
                $scope.settings = settings;
            });
        }

        loadJenkinsSettings();
    })
;