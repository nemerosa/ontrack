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
            $scope.loadingProjects = true;
            ot.pageCall($http.get('structure/projects/view')).then(function (projectStatusViewResources) {
                $scope.projectStatusViewResources = projectStatusViewResources;
                $scope.projectStatusViews = projectStatusViewResources.resources;
                // Commands
                $rootScope.view.commands = [
                    {
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        condition: function () {
                            return projectStatusViewResources._create;
                        },
                        action: function () {
                            otStructureService.createProject(projectStatusViewResources._create).then(loadProjects);
                        }
                    }, {
                        id: 'showDisabled',
                        name: "Show all hidden items",
                        cls: 'ot-command-show-disabled',
                        condition: function () {
                            return !$scope.showDisabled;
                        },
                        action: function () {
                            $scope.showDisabled = true;
                        }
                    }, {
                        id: 'hideDisabled',
                        name: "Hide disabled items",
                        cls: 'ot-command-hide-disabled',
                        condition: function () {
                            return $scope.showDisabled;
                        },
                        action: function () {
                            $scope.showDisabled = false;
                        }
                    }, {
                        name: "API",
                        cls: 'ot-command-api',
                        link: '/api-doc'
                    }
                ];
            }).finally(function () {
                $scope.loadingProjects = false;
            });
            // Any notification?
            if (code) {
                if (code == 403) {
                    otNotificationService.error("Due to the access to an unauthorized resource, you have been redirected to the home page.");
                }
            }
        }

        loadProjects();

    })
;