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
    .controller('HomeCtrl', function ($rootScope, $location, $scope, otStructureService, otNotificationService) {
        var code = $location.search().code;
        $rootScope.view = {
            // TODO Breadcrumbs
            // Title
            title: 'Home',
            // Commands
            commands: []
        };
        // Loading the project list
        function loadProjects() {
            otStructureService.getProjects().then(function (projectCollection) {
                $scope.projectCollection = projectCollection;
                // TODO Loading the projects' views
                // Commands
                $rootScope.view.commands = [
                    {
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        condition: function () {
                            return projectCollection.create;
                        },
                        action: function () {
                            otStructureService.createProject(projectCollection.create.href).then(loadProjects);
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