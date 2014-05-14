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
    .controller('HomeCtrl', function ($rootScope, $scope, otStructureService) {
        $rootScope.view = {
            // TODO Breadcrumbs
            // Title
            title: 'Home'
            // TODO Commands
        };
        otStructureService.getProjects().then(function (projectCollection) {
            $scope.projectCollection = projectCollection;
            // TODO Loading the projects' views
            // TODO Creating a project
        });
    })
;