angular.module('ot.view.home', [
    'ui.router',
    'ot.service.structure',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            templateUrl: 'app/view/view.home.tpl.html',
            controller: 'HomeCtrl'
        });
    })
    .controller('HomeCtrl', function ($rootScope, $location, $scope, $http, ot, otStructureService, otNotificationService) {
        var code = $location.search().code;
        $rootScope.view = {
            // Title
            title: 'Home',
            // Commands
            commands: []
        };
        // Loading the project list
        function loadProjects() {
            otStructureService.getProjects().then(function (projectCollection) {
                $scope.projectCollection = projectCollection;
                // Loading the projects' views
                angular.forEach($scope.projectCollection.resources, function (project) {
                    ot.call($http.get(project._branchStatusViews)).then(function (branchStatusViews) {
                        project.branchStatusViews = branchStatusViews;
                    });
                });
                // Commands
                $rootScope.view.commands = [
                    {
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        condition: function () {
                            return projectCollection._create;
                        },
                        action: function () {
                            otStructureService.createProject(projectCollection._create).then(loadProjects);
                        }
                    }
                ];
            });
            // Any notification?
            if (code) {
                if (code == 403) {
                    otNotificationService.error("Due to the access to an unauthorized page, you have been redirected to the home page.");
                }
            }
        }

        loadProjects();

    })
;