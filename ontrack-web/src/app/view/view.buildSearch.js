angular.module('ot.view.buildSearch', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.copy'
])
    .config(function ($stateProvider) {
        $stateProvider.state('build-search', {
            url: '/build-search/{projectId}',
            templateUrl: 'app/view/view.buildSearch.tpl.html',
            controller: 'BuildSearchCtrl'
        });
    })
    .controller('BuildSearchCtrl', function ($scope, $stateParams, $state, $http, ot, otStructureService, otAlertService, otCopyService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;

        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.breadcrumbs = ot.projectBreadcrumbs(projectResource);
                view.title = "Build search";
                // View commands
                view.commands = [
                    ot.viewCloseCommand('/project/' + projectResource.id)
                ];
            });
        }

        // Initialization
        loadProject();

    })
;