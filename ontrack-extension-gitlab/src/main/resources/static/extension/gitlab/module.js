angular.module('ontrack.extension.gitlab', [
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // GitLab configurations
        $stateProvider.state('gitlab-configurations', {
            url: '/extension/gitlab/configurations',
            templateUrl: 'extension/gitlab/gitlab.configurations.tpl.html',
            controller: 'GitLabConfigurationsCtrl'
        });
    })
    .controller('GitLabConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'GitLab configurations';
        view.description = 'Management of the GitLab configurations.';
        view.commands = [];

        // Loading the Artifactory configurations
        function load() {
            ot.call($http.get('extension/gitlab/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'gitlab-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        load();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "GitLab configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'GiLab configuration',
                message: "Do you really want to delete this GitLab configuration? Some projects may still refer to it."
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
                title: "GitLab configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })
;