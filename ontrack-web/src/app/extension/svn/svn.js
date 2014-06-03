angular.module('ontrack.extension.svn', [
    'ui.router',
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-configurations', {
            url: '/extension/svn/configurations',
            templateUrl: 'app/extension/svn/svn.configurations.tpl.html',
            controller: 'SVNConfigurationsCtrl'
        });
    })
    .controller('SVNConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        var view = ot.view();
        view.title = 'SVN configurations';
        view.description = 'Management of the SVN configurations.';

        // Loading the SVN configurations
        function loadSVNConfigurations() {
            ot.call($http.get('extension/svn/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'svn-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadSVNConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "SVN configuration",
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadSVNConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this SVN configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadSVNConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "SVN configuration",
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadSVNConfigurations);
        };
    })
;