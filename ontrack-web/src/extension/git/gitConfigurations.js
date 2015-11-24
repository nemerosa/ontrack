angular.module('ot.extension.git.configuration', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('git-configurations', {
            url: '/extension/git/configurations',
            templateUrl: 'extension/git/git.configurations.tpl.html',
            controller: 'GitConfigurationsCtrl'
        });
    })
    .controller('GitConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'Git configurations';
        view.description = 'Management of the Git configurations.';

        // Loading the Artifactory configurations
        function load() {
            ot.call($http.get('extension/git/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'git-configuration-create',
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
                title: "Git configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Git configuration',
                message: "Do you really want to delete this Git configuration? Some projects may still refer to it."
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
                title: "Git configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })
;