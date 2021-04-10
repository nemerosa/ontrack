angular.module('ot.view.home', [
    'ui.router',
    'ot.service.structure',
    'ot.service.core',
    'ot.service.user',
    'ot.service.graphql',
    'ot.service.label'
])
    .config(function ($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            templateUrl: 'app/view/view.home.tpl.html',
            controller: 'HomeCtrl'
        });
    })
    .controller('HomeCtrl', function ($rootScope, $location, $log, $scope, $http, ot, otGraphqlService, otStructureService, otNotificationService, otUserService, otLabelService) {
        const search = $location.search();
        const code = search.code;
        const url = search.url;
        $rootScope.view = {
            // Title
            title: 'Home',
            // Commands
            commands: []
        };

        // Preloading filter mode
        $scope.preloadingLabelFilter = true;

        // No initial filter
        $scope.projectFilter = {
            name: '',
            label: undefined
        };

        // GraphQL fragment for the decorations
        const decorationFragment = `
            fragment decorationContent on Decoration {
              decorationType
              error
              data
              feature {
                id
              }
            }
        `;

        // GraphQL fragment for the displayed projects
        const projectFragment = `
            fragment projectContent on Project {
                id
                name
                labels {
                  id
                  category
                  name
                  description
                  color
                  foregroundColor
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
        `;

        // Full GraphQL query
        const fullQuery = ` query HomePage(
                    $includeProjects: Boolean!,
                    $maxBranches: Int!
                ) {
                  userRootActions {
                    projectCreate
                  }
                  labels {
                    id
                    category
                    name
                    description
                    color
                    foregroundColor
                    computedBy {
                        id
                        name
                    }
                    links {
                        _update
                        _delete
                    }
                  }
                  favouriteBranches: branches(favourite: true) {
                    project {
                      name
                      links {
                        _page
                      }
                    }
                    id
                    name
                    disabled
                    type
                    decorations {
                      ...decorationContent
                    }
                    creation {
                      time
                    }
                    links {
                      _page
                      _enable
                      _disable
                      _delete
                      _unfavourite
                      _favourite
                    }
                    latestBuild: builds(count: 1) {
                      id
                      name
                      creation {
                          time
                      }
                    }
                    promotionLevels {
                      id
                      name
                      image
                      _image
                      promotionRuns(first: 1) {
                        build {
                          id
                          name
                        }
                      }
                    }
                  }
                  projects @include(if: $includeProjects) {
                    ...projectContent
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
                    branches(useModel: true, count: $maxBranches) {
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
                
                ${projectFragment}
                
                ${decorationFragment}
                `;

        // Projects by name
        const projectQuery = `
            query ProjectQuery(pattern: String!) {
                projects(pattern: $pattern) {
                    ...projectContent
                }
            }
                
            ${projectFragment}
            
            ${decorationFragment}
        `;

        // Loading the project list
        function loadProjects() {
            $scope.loadingProjects = true;
            // We start collecting some global settings
            otGraphqlService.pageGraphQLCall(`{
                settings {
                    homePage {
                        maxBranches
                        maxProjects
                    }
                }
                entityCounts {
                    projects
                }
            }`).then(global => {
                // Storing the data
                $scope.maxBranches = global.settings.homePage.maxBranches;
                $scope.maxProjects = global.settings.homePage.maxProjects;
                $scope.projectCount = global.entityCounts.projects;
                // Must the projects be included?
                $scope.includeProjects = $scope.projectCount <= $scope.maxProjects;
                // Actual call
                return otGraphqlService.pageGraphQLCall(fullQuery, {
                    includeProjects: $scope.includeProjects,
                    maxBranches: $scope.maxBranches
                });
            }).then(function (data) {

                $scope.projectsData = data;
                $scope.projectFavourites = data.projectFavourites;
                $scope.favouriteBranches = data.favouriteBranches;

                if ($scope.includeProjects) {
                    preloadLabelFilter();
                }

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

        // Search for the projects by name
        // TODO Fuzzy search is needed (name part only)
        $scope.onProjectSearch = () => {
            if ($scope.projectFilter.name) {
                $scope.searchingProjects = true;
                otGraphqlService.pageGraphQLCall(projectQuery, { pattern: $scope.projectFilter.name}).then(data => {
                    $scope.projectsData.projects = data.projects;
                    $scope.searchingReturnsNoResult = (data.projects.length === 0);
                }).finally(() => {
                    $scope.searchingProjects = false;
                });
            }
        };

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

        // Sets a branch as favourite
        $scope.branchFavourite = function (branch) {
            if (branch.links._favourite) {
                ot.pageCall($http.put(branch.links._favourite)).then(loadProjects);
            }
        };

        // Unsets a branch as favourite
        $scope.branchUnfavourite = function (branch) {
            if (branch.links._unfavourite) {
                ot.pageCall($http.put(branch.links._unfavourite)).then(loadProjects);
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

        const LOCATION_KEY = "label";
        const STORAGE_KEY = "home.label";

        // Management of the location search component in case of label change
        $scope.$watch('projectFilter.label', function () {
            if (!$scope.preloadingLabelFilter) {
                if ($scope.projectFilter.label) {
                    let labelKey = $scope.formatLabel($scope.projectFilter.label);
                    $location.search(LOCATION_KEY, labelKey);
                    localStorage.setItem(STORAGE_KEY, labelKey);
                } else {
                    $location.search(LOCATION_KEY, undefined);
                    localStorage.removeItem(STORAGE_KEY);
                }
            }
        });

        // Preloading of the label filter
        function preloadLabelFilter() {
            // Gets the label key
            let labelKey = getLabelKeyFromBrowser();
            // If defined, identifies and sets the filter
            let label = $scope.projectsData.labels.find(it => {
                return $scope.formatLabel(it) === labelKey;
            });
            if (label) {
                $scope.projectFilter.label = label;
            }
            // Done
            $scope.preloadingLabelFilter = false;
        }

        function getLabelKeyFromBrowser() {
            let key = $location.search().label;
            if (key) {
                return key;
            } else {
                return localStorage.getItem(STORAGE_KEY);
            }
        }

        // Filtering the labels for a token
        $scope.typeAheadFilterLabels = (token) => {
            return $scope.projectsData.labels.filter(label => {
                return !token || ($scope.formatLabel(label).toLowerCase().indexOf(token.toLowerCase()) >= 0);
            });
        };

        // Formatting a label for a type-ahead
        $scope.formatLabel = otLabelService.formatLabel;

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