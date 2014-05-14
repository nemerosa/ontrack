angular.module('ot.view.project', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($scope, $stateParams, ot, otStructureService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;
        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.title = projectResource.name;
                // View commands
                view.commands = [
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadProject();
        // TODO Loading the project's view
        // TODO Project commands
    })
;