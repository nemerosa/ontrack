angular.module('ontrack.extension.bitbucket-cloud', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        $stateProvider.state('bitbucket-cloud-configurations', {
            url: '/extension/bitbucket-cloud/configurations',
            templateUrl: 'extension/bitbucket-cloud/configurations.tpl.html',
            controller: 'BitbucketCloudConfigurationsCtrl'
        });
    })
    .controller('BitbucketCloudConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'Bitbucket Cloud configurations';
        view.description = 'Management of the Bitbucket Cloud configurations.';

        // Loading the configurations
        function load() {
            ot.call($http.get('extension/bitbucket-cloud/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'bitbucket-cloud-configuration-create',
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
                title: "Bitbucket Cloud configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Bitbucket Cloud configuration',
                message: "Do you really want to delete this Bitbucket Cloud configuration? Some projects may still refer to it."
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
                title: "Bitbucket Cloud configuration",
                buttons: [ otConfigurationService.testButton($scope.configurations._test) ],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })
;
