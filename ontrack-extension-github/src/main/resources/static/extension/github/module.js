angular.module('ontrack.extension.github', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('github-configurations', {
            url: '/extension/github/configurations',
            templateUrl: 'extension/github/github.configurations.tpl.html',
            controller: 'GitHubConfigurationsCtrl'
        });
    })
    .controller('GitHubConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService, otGraphqlService) {
        var view = ot.view();
        view.title = 'GitHub configurations';
        view.description = 'Management of the GitHub configurations.';
        view.commands = [];

        // Query to get the list of configurations
        const query = `
            {
                gitHubConfigurations {
                    name
                    url
                    user
                    appId
                    appInstallationAccountName
                    links {
                        _update
                        _delete
                    }
                }
            }
        `;

        // Loading the configurations
        function load() {
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.configurations = data.gitHubConfigurations;
                view.commands = [
                    {
                        id: 'github-configuration-create',
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
                uri: '/extension/github/configurations/create',
                title: "GitHub configuration",
                buttons: [ otConfigurationService.testButton('/extension/github/configurations/test') ],
                submit: function (data) {
                    return ot.call($http.post('/extension/github/configurations/create', data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'GitHub configuration',
                message: "Do you really want to delete this GitHub configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration.links._delete)).then(load);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration.links._update,
                title: "GitHub configuration",
                buttons: [ otConfigurationService.testButton('/extension/github/configurations/test') ],
                submit: function (data) {
                    return ot.call($http.put(configuration.links._update, data));
                }
            }).then(load);
        };
    })
;