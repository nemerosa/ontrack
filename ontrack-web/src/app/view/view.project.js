angular.module('ot.view.project', [
    'ui.router',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($rootScope, $scope, otStructureService) {
        // TODO Breadcrumbs
        // TODO Title
        // TODO Loading the project
        // TODO Loading the project's view
        // TODO Project commands
    })
;