angular.module('ontrack.extension.tfc', [
    'ui.router',
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        $stateProvider.state('tfc-configurations', {
            url: '/extension/tfc/configurations',
            templateUrl: 'extension/tfc/tfc.configurations.tpl.html',
            controller: 'TFCConfigurationsCtrl'
        });
    })
    .controller('TFCConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'TFC configurations';
        view.description = 'Management of the Terraform Cloud / Enterprise configurations.';

        // Loading the configurations
        function load() {
            ot.call($http.get('extension/tfc/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'tfc-configuration-create',
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
                title: "TFC/TFE configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'TFC/TFE configuration',
                message: "Do you really want to delete this configuration? Some projects may still refer to it."
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
                title: "TFC/TFE configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })
;