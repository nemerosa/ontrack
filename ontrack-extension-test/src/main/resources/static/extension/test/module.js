angular.module('ontrack.extension.test', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('extension-test-projects', {
            url: '/extension/test/projects',
            templateUrl: 'extension/test/projects.tpl.html',
            controller: 'ExtensionTestProjectsCtrl'
        });
    })
    .controller('ExtensionTestProjectsCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "List of projects";

        const query = `
            query Projects {
                projects {
                    name
                    links {
                        _page
                    }
                }
            }
        `;

        otGraphqlService.pageGraphQLCall(query, {}).then(data => {
            $scope.projects = data.projects;
        });
    })
;
