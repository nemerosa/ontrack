angular.module('ontrack.extension.svn', [
    'ui.router',
    'ontrack.extension.scm',
    'ot.service.core',
    'ot.service.form',
    'ot.extension.svn.changelog',
    'ot.extension.svn.revision',
    'ot.extension.svn.issue',
    'ot.extension.svn.sync',
    'ot.extension.svn.dialog.indexation'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-configurations', {
            url: '/extension/svn/configurations',
            templateUrl: 'app/extension/svn/svn.configurations.tpl.html',
            controller: 'SVNConfigurationsCtrl'
        });

    })
    .controller('SVNConfigurationsCtrl', function ($scope, $http, $modal, ot, otFormService, otAlertService) {
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
                    ot.viewApiCommand(configurations._self),
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

        // Configuration indexation
        $scope.indexation = function (configuration) {
            $modal.open({
                templateUrl: 'app/extension/svn/svn.dialog.indexation.tpl.html',
                controller: 'svnDialogIndexation',
                resolve: {
                    config: function () {
                        return {
                            configuration: configuration
                        };
                    }
                }
            });
        };

    })
    .directive('otExtensionSvnRevisionSummary', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/svn/directive.revision.summary.tpl.html',
            scope: {
                revisionInfo: '=',
                title: '@'
            }
        };
    })
    .directive('otExtensionSvnRevisionBuilds', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/svn/directive.revision.builds.tpl.html',
            scope: {
                ontrackSvnRevisionInfo: '=',
                mergedRevisionInfos: '='
            }
        };
    })
    .directive('otExtensionSvnRevisionPromotions', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/svn/directive.revision.promotions.tpl.html',
            scope: {
                ontrackSvnRevisionInfo: '=',
                mergedRevisionInfos: '='
            }
        };
    })
;