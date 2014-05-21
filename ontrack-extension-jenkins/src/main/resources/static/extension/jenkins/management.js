angular.module('ontrack.extension.jenkins', [
    'ui.router'
])
    .config(function ($stateProvider) {
        // Jenkins management
        $stateProvider.state('jenkins-management', {
            url: '/extension/jenkins/management',
            templateUrl: 'extension/jenkins/view.management.tpl.html',
            controller: 'JenkinsManagementCtrl'
        });
    })
    .controller('JenkinsManagementCtrl', function ($scope) {

    })
;