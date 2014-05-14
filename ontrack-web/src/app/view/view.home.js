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
                $rootScope.view.commands = [];
                // Creating a project
                if (projectCollection.create) {
                    $rootScope.view.commands.push({
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        action: function () {
                            otStructureService.createProject(projectCollection.create.href).then(loadProjects);
                        }
                    });
                }
            });
        }

        loadProjects();
    })
;