angular.module('ot.view.home', [
    'ui.router',
    'ot.service.structure',
    'ot.service.core',
    'ot.service.user',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            templateUrl: 'app/view/view.home.tpl.html',
            controller: 'HomeCtrl'
        });
    })
    .controller('HomeCtrl', function ($rootScope, $location, $log, $scope, $http, ot, otGraphqlService, otStructureService, otNotificationService, otUserService) {
        var search = $location.search();
        var code = search.code;
        var url = search.url;
        $rootScope.view = {
            // Title
            title: 'Home',
            // Commands
            commands: []
        };
        // No initial filter
        $scope.projectFilter = {
            name: ''
        };

        // Loading the project list
        function loadProjects() {
            $scope.loadingProjects = true;
            otGraphqlService.pageGraphQLCall("{\n" +
                "  userRootActions {\n" +
                "    projectCreate\n" +
                "  }\n" +
                "  projects {\n" +
                "    id\n" +
                "    name\n" +
                "    links {\n" +
                "      _favourite\n" +
                "      _unfavourite\n" +
                "    }\n" +
                "    decorations {\n" +
                "      ...decorationContent\n" +
                "    }\n" +
                "  }\n" +
                "  projectFavourites: projects(favourites: true) {\n" +
                "    id\n" +
                "    name\n" +
                "    disabled\n" +
                "    decorations {\n" +
                "      ...decorationContent\n" +
                "    }\n" +
                "    links {\n" +
                "      _unfavourite\n" +
                "    }\n" +
                "    branches {\n" +
                "      id\n" +
                "      name\n" +
                "      type\n" +
                "      disabled\n" +
                "      decorations {\n" +
                "        ...decorationContent\n" +
                "      }\n" +
                "      latestPromotions: builds(lastPromotions: true) {\n" +
                "        id\n" +
                "        name\n" +
                "        promotionRuns {\n" +
                "          promotionLevel {\n" +
                "            id\n" +
                "            name\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "      latestBuild: builds(count: 1) {\n" +
                "        id\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment decorationContent on Decoration {\n" +
                "  decorationType\n" +
                "  error\n" +
                "  data\n" +
                "  feature {\n" +
                "    id\n" +
                "  }\n" +
                "}\n").then(function (data) {

                $scope.projectsData = data;
                $scope.projectFavourites = data.projectFavourites;

                // All branches disabled status computation
                $scope.projectFavourites.forEach(function (projectFavourite) {
                    projectFavourite.allBranchesDisabled = projectFavourite.branches.length > 0 &&
                        projectFavourite.branches.every(function (branch) {
                            return branch.disabled || branch.type === 'TEMPLATE_DEFINITION';
                        });
                });

                // Commands
                $rootScope.view.commands = [
                    {
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        condition: function () {
                            return data.userRootActions.projectCreate;
                        },
                        action: $scope.createProject
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
                    }, {
                        name: "GraphiQL",
                        cls: 'ot-command-api',
                        absoluteLink: "graphiql.html"
                    }
                ];
            }).finally(function () {
                $scope.loadingProjects = false;
            });

        }

        // Creating a project
        $scope.createProject = function () {
            otStructureService.createProject($scope.projectsData.userRootActions.projectCreate).then(loadProjects);
        };

        // Sets a project as favourite
        $scope.projectFavourite = function (project) {
            if (project.links._favourite) {
                ot.pageCall($http.put(project.links._favourite)).then(loadProjects);
            }
        };

        // Unsets a project as favourite
        $scope.projectUnfavourite = function (project) {
            if (project.links._unfavourite) {
                ot.pageCall($http.put(project.links._unfavourite)).then(loadProjects);
            }
        };

        // Login procedure
        $scope.accessStatus = 'undefined';
        if (code && code == 403) {
            $log.debug('[403] received');
            if (otUserService.logged()) {
                $scope.accessStatus = 'unauthorised';
                $log.debug('[403] user already logged - error notification');
            } else {
                $scope.accessStatus = 'login-requested';
                $log.debug('[403] user not logged - login redirection - callback URL = ' + url);
                otUserService.login().then(function () {
                    $scope.accessStatus = 'ok';
                    if (url) {
                        // Callback URL
                        $log.debug('[403] reloading ' + url + 'after signing in.');
                        location.href = url;
                        location.reload();
                    } else {
                        // Reloads current page
                        $log.debug('[403] reloading after signing in.');
                        location.reload();
                    }
                }, function () {
                    $scope.accessStatus = 'login-failed';
                    $log.debug('[403] user cannot be logged - error notification');
                });
            }
        } else if ($rootScope.user && $rootScope.user.authenticationRequired && !$rootScope.user.logged) {
            $scope.accessStatus = 'login-requested';
            $log.debug('user not logged and authentication is required');
            otUserService.login().then(function () {
                $scope.accessStatus = 'ok';
                $log.debug('[403] reloading after signing in.');
                location.reload();
            }, function () {
                $scope.accessStatus = 'login-failed';
                $log.debug('[403] user cannot be logged - error notification');
            });
        } else {
            $scope.accessStatus = 'ok';
            // Loading the list of projects
            loadProjects();
        }

    })
;