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
        const search = $location.search();
        const code = search.code;
        const url = search.url;
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
            otGraphqlService.pageGraphQLCall(`{
              userRootActions {
                projectCreate
              }
              labels {
                id
                category
                name
                description
                color
                computedBy {
                    id
                    name
                }
                links {
                    _update
                    _delete
                }
              } 
              projects {
                id
                name
                labels {
                  id
                  category
                  name
                  description
                  color
                  computedBy {
                    id
                    name
                  }
                }
                links {
                  _favourite
                  _unfavourite
                }
                decorations {
                  ...decorationContent
                }
              }
              projectFavourites: projects(favourites: true) {
                id
                name
                disabled
                decorations {
                  ...decorationContent
                }
                links {
                  _unfavourite
                }
                branches {
                  id
                  name
                  type
                  disabled
                  decorations {
                    ...decorationContent
                  }
                  latestPromotions: builds(lastPromotions: true, count: 1) {
                    id
                    name
                    promotionRuns {
                      promotionLevel {
                        id
                        name
                        image
                        _image
                      }
                    }
                  }
                  latestBuild: builds(count: 1) {
                    id
                    name
                  }
                }
              }
            }
            
            fragment decorationContent on Decoration {
              decorationType
              error
              data
              feature {
                id
              }
            }
            `).then(function (data) {

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

        // Selecting a label
        $scope.projectFilterSelectLabel = (label) => {
            $scope.projectFilter.label = label;
        };

        // Clearing the label selection
        $scope.projectFilterClearLabel = () => {
            $scope.projectFilter.label = undefined;
        };

        // Filtering the labels for a token
        $scope.typeAheadFilterLabels = (token) => {
            return $scope.projectsData.labels.filter(label => {
                return !token || ($scope.formatLabel(label).toLowerCase().indexOf(token.toLowerCase()) >= 0);
            });
        };

        // Formatting a label for a type-ahead
        $scope.formatLabel = (label) => {
            if (!label) {
                return "";
            } else if (label.category) {
                return `${label.category}:${label.name}`;
            } else {
                return label.name;
            }
        };

        // Project filter function
        $scope.projectFilterFn = (project) => {
            return projectFilterNameFn(project) && projectFilterLabelFn(project);
        };

        const projectFilterNameFn = project => {
            return !$scope.projectFilter.name || project.name.toLowerCase().indexOf($scope.projectFilter.name.toLowerCase()) >= 0;
        };

        const projectFilterLabelFn = project => {
            return !$scope.projectFilter.label || project.labels.some(it => it.id === $scope.projectFilter.label.id);
        };

        // Login procedure
        $scope.accessStatus = 'undefined';
        if (code && Number(code) === 403) {
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