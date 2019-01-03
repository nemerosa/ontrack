angular.module('ot.view.project', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.copy',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($scope, $stateParams, $state, $http, ot, otGraphqlService, otStructureService, otAlertService, otCopyService) {
        const view = ot.view();
        // Project's id
        const projectId = $stateParams.projectId;
        // Initial name filter
        $scope.branchNameFilter = '';

        // Loading the project and its whole information
        function loadProject() {
            $scope.loadingBranches = true;
            otGraphqlService.pageGraphQLCall(`query ProjectView($projectId: Int) {
              projects(id: $projectId) {
                id
                name
                description
                disabled
                decorations {
                  ...decorationContent
                }
                links {
                  _self
                  _createBranch
                  _update
                  _delete
                  _permissions
                  _clone
                  _enable
                  _disable
                  _properties
                  _extra
                  _events
                  _actions
                }
                branches {
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
              }
            }
            
            fragment decorationContent on Decoration {
              decorationType
              error
              data
              feature {
                id
              }
            }`, {projectId: projectId}).then(function (data) {
                $scope.project = data.projects[0];
                // View commands
                view.commands = [
                    {
                        condition: function () {
                            return $scope.project.links._createBranch;
                        },
                        id: 'createBranch',
                        name: "Create branch",
                        cls: 'ot-command-branch-new',
                        action: function () {
                            otStructureService.create($scope.project.links._createBranch, "New branch").then(loadProject);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project.links._update;
                        },
                        id: 'updateProject',
                        name: "Update project",
                        cls: 'ot-command-project-update',
                        action: function () {
                            otStructureService.update(
                                $scope.project.links._update,
                                "Update project"
                            ).then(loadProject);
                        }
                    },
                    {
                        id: 'searchBuild',
                        name: "Search builds",
                        cls: 'ot-command-project-search-builds',
                        link: '/build-search/' + $scope.project.id
                    },
                    {
                        condition: function () {
                            return $scope.project.links._disable;
                        },
                        id: 'disableProject',
                        name: "Disable project",
                        cls: 'ot-command-project-disable',
                        action: function () {
                            ot.pageCall($http.put($scope.project.links._disable)).then(loadProject);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project.links._enable;
                        },
                        id: 'enableProject',
                        name: "Enable project",
                        cls: 'ot-command-project-enable',
                        action: function () {
                            ot.pageCall($http.put($scope.project.links._enable)).then(loadProject);
                        }
                    }, {
                        id: 'showDisabled',
                        name: "Show all branches",
                        cls: 'ot-command-show-disabled',
                        condition: function () {
                            return !$scope.showDisabled;
                        },
                        action: function () {
                            $scope.showDisabled = true;
                        }
                    }, {
                        id: 'hideDisabled',
                        name: "Hide disabled branches",
                        cls: 'ot-command-hide-disabled',
                        condition: function () {
                            return $scope.showDisabled;
                        },
                        action: function () {
                            $scope.showDisabled = false;
                        }
                    },
                    {
                        // FIXME Condition for the edition
                        id: 'labelsProject',
                        name: "Labels",
                        cls: 'ot-command-project-labels',
                        action: editProjectLabels
                    },
                    {
                        condition: function () {
                            return $scope.project.links._permissions;
                        },
                        id: 'permissionsProject',
                        name: "Permissions",
                        cls: 'ot-command-project-permissions',
                        link: '/admin-project-acl/' + $scope.project.id
                    },
                    {
                        condition: function () {
                            return $scope.project.links._clone;
                        },
                        id: 'cloneProject',
                        name: "Clone project",
                        cls: 'ot-command-project-clone',
                        action: function () {
                            otStructureService.getProject($scope.project.id).then(function (project) {
                                otCopyService.cloneProject(project).then(function (newProject) {
                                    $state.go('project', {
                                        projectId: newProject.id
                                    });
                                });
                            });
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project.links._delete;
                        },
                        id: 'deleteProject',
                        name: "Delete project",
                        cls: 'ot-command-project-delete',
                        action: function () {
                            otAlertService.confirm({
                                title: "Deleting a project",
                                message: "Do you really want to delete the project " + $scope.project.name +
                                " and all its associated data?"
                            }).then(function () {
                                return ot.call($http.delete($scope.project.links._delete));
                            }).then(function () {
                                $state.go('home');
                            });
                        }
                    },
                    ot.viewApiCommand($scope.project.links._self),
                    ot.viewActionsCommand($scope.project.links._actions),
                    ot.viewCloseCommand('/home')
                ];
            }).finally(function () {
                $scope.loadingBranches = false;
            });
        }

        // Initialization
        loadProject();

        // Reload callback available in the scope
        $scope.reloadProject = loadProject;

        // Time to use for sorting branches
        $scope.getBranchTime = function (branch) {
            if (branch.latestBuild && branch.latestBuild.length > 0) {
                return branch.latestBuild[0].creation.time;
            } else {
                return branch.creation.time;
            }
        };

        // Enabling a branch
        $scope.enableBranch = function (branch) {
            if (branch.links._enable) {
                ot.pageCall($http.put(branch.links._enable)).then(loadProject);
            }
        };

        // Disabling a branch
        $scope.disableBranch = function (branch) {
            if (branch.links._disable) {
                ot.pageCall($http.put(branch.links._disable)).then(loadProject);
            }
        };

        // Deleting a branch
        $scope.deleteBranch = function (branch) {
            return otAlertService.confirm({
                title: "Deleting a branch",
                message: "Do you really want to delete the branch " + branch.name +
                " and all its associated data?"
            }).then(function () {
                return ot.call($http.delete(branch.links._delete));
            }).then(loadProject);
        };

        // FIXME Editing the project labels
        function editProjectLabels() {
        }
    })
;