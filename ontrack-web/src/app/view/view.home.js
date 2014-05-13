angular.module('ot.view.home', [
    'ui.router',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            templateUrl: 'app/view/view.home.tpl.html',
            controller: 'HomeCtrl'
        });
    })
    .controller('HomeCtrl', function ($scope, otStructureService) {
        // TODO Breadcrumbs
        // TODO Title
        // TODO Commands
        // TODO Loading the projects
        otStructureService.getProjects().then(function (projectCollection) {
            $scope.projectCollection = projectCollection;
            // TODO Loading the projects' views
            // TODO Creating a project
        });
    })
;