angular.module('ot.view.home', [
    'ui.router',
    'ot.service.structure',
    'ot.service.core',
    'ot.service.action',
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
    .controller('HomeCtrl', function ($rootScope, $location, $log, $scope, $http, ot, otGraphqlService, otStructureService, otNotificationService, otUserService, otLabelService, otActionService) {
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


        // Loading the project list
        function loadProjects() {
            $scope.loadingProjects = true;
            otGraphqlService.pageGraphQLCall(`{
              user {
                actions {
                  createProject {
                    links {
                      form {
                        description
                        method
                        uri
                      }
                    }
                    mutation
                  }
                }
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
              projects {
                id
                name
                favourite
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
                actions {
                  favouriteProject {
                    description
                    mutation
                  }
                  unfavouriteProject {
                    description
                    mutation
                  }
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
                actions {
                  unfavouriteProject {
                    description
                    mutation
                  }
                }
                branches(useModel: true) {
                  id
                  name
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
                $scope.favouriteBranches = data.favouriteBranches;

                preloadLabelFilter();

                // All branches disabled status computation
                $scope.projectFavourites.forEach(function (projectFavourite) {
                    projectFavourite.allBranchesDisabled = projectFavourite.branches.length > 0 &&
                        projectFavourite.branches.every(function (branch) {
                            return branch.disabled;
                        });
                });

                // Commands
                $rootScope.view.commands = [
                    {
                        id: 'createProject',
                        name: 'Create project',
                        cls: 'ot-command-project-new',
                        condition: function () {
                            return data.user.actions.createProject.mutation;
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
            otActionService.runActionForm(
                $scope.projectsData.user.actions.createProject,
                {
                    query: `
                        mutation CreateProject($name: String!, $description: String, $disabled: Boolean!) {
                            createProject(input: {name: $name, description: $description, disabled: $disabled}) {
                                errors {
                                    message
                                    exception
                                }
                            }
                        }     
                    `,
                    variables: data => ({
                        name: data.name,
                        description: data.description,
                        disabled: data.disabled == null ? false : data.disabled
                    })
                }
            ).then(loadProjects);
        };

        // Sets a project as favourite
        $scope.projectFavourite = function (project) {
            if (project.actions.favouriteProject.mutation) {
                otActionService.runMutationAction(
                    project.actions.favouriteProject,
                    {
                        query: `
                            mutation FavouriteProject($id: Int!) {
                                favouriteProject(input: {id: $id}) {
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }     
                        `,
                        variables: () => ({
                            id: project.id
                        })
                    }
                ).then(loadProjects);
            }
        };

        // Unsets a project as favourite
        $scope.projectUnfavourite = function (project) {
            if (project.actions.unfavouriteProject.mutation) {
                otActionService.runMutationAction(
                    project.actions.unfavouriteProject,
                    {
                        query: `
                            mutation UnfavouriteProject($id: Int!) {
                                unfavouriteProject(input: {id: $id}) {
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }     
                        `,
                        variables: () => ({
                            id: project.id
                        })
                    }
                ).then(loadProjects);
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

        // Loading the list of projects
        loadProjects();

    })
;