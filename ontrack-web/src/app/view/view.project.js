angular.module('ot.view.project', [
    'ui.router',
    'ot.service.action',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.copy',
    'ot.service.graphql',
    'ot.service.label',
    'ot.dialog.project.labels'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($modal, $scope, $stateParams, $state, $http, ot, otGraphqlService, otStructureService, otAlertService, otCopyService, otLabelService, otActionService) {
        const view = ot.view();
        // Project's id
        const projectId = $stateParams.projectId;
        // Initial name filter
        $scope.branchFilter = {
            name: ""
        };

        // Loading the project and its whole information
        function loadProject() {
            $scope.loadingBranches = true;
            otGraphqlService.pageGraphQLCall(`query ProjectView($projectId: Int) {
              projects(id: $projectId) {
                id
                name
                description
                annotatedDescription
                disabled
                decorations {
                  ...decorationContent
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
                }
                actions {
                    updateProject {
                        links {
                            form {
                                description
                                method
                                uri
                            }
                        }
                        mutation
                    }
                    deleteProject {
                        mutation
                    }
                }
                links {
                  _self
                  _createBranch
                  _permissions
                  _clone
                  _enable
                  _disable
                  _properties
                  _extra
                  _events
                  _actions
                  _labels
                  _labelFromToken
                  _labelsCreate
                }
                favouriteBranches: branches(favourite: true) {
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
                branches {
                  id
                  name
                  disabled
                  decorations {
                    ...decorationContent
                  }
                  creation {
                    time
                  }
                  latestBuild: builds(count: 1) {
                    creation {
                        time
                    }
                  }
                  links {
                    _page
                    _enable
                    _disable
                    _delete
                    _unfavourite
                    _favourite
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
                        condition: () => $scope.project.actions.updateProject.mutation,
                        id: 'updateProject',
                        name: "Update project",
                        cls: 'ot-command-project-update',
                        action: updateProject
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
                        condition: function () {
                            return $scope.project.links._labels;
                        },
                        id: 'labelsProject',
                        name: "Labels",
                        cls: 'ot-command-project-labels',
                        action: () => {
                            $scope.editProjectLabels();
                        }
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
                        condition: () => $scope.project.actions.deleteProject.mutation,
                        id: 'deleteProject',
                        name: "Delete project",
                        cls: 'ot-command-project-delete',
                        action: deleteProject
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

        // Deleting the project
        const deleteProject = () => {
            otAlertService.confirm({
                title: "Deleting a project",
                message: `Do you really want to delete the project ${$scope.project.name} and all its associated data?`
            }).then(() => otActionService.runMutationAction(
                $scope.project.actions.deleteProject,
                {
                    query: `
                        mutation DeleteProject($id: Int!) {
                            deleteProject(input: {id: $id}) {
                                errors {
                                    message
                                    exception
                                }
                            }
                        }     
                    `,
                    variables: () => ({
                        id: $scope.project.id
                    })
                }
            )).then(() => $state.go('home'));
        };

        // Updating the project
        const updateProject = () => {
            otActionService.runActionForm(
                $scope.project.actions.updateProject,
                {
                    query: `
                        mutation UpdateProject($id: Int!, $name: String!, $description: String, $disabled: Boolean!) {
                            updateProject(input: {id: $id, name: $name, description: $description, disabled: $disabled}) {
                                project {
                                    name
                                    description
                                    annotatedDescription
                                    disabled
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }     
                    `,
                    variables: data => ({
                        id: $scope.project.id,
                        name: data.name,
                        description: data.description,
                        disabled: data.disabled == null ? false : data.disabled
                    })
                }
            ).then((data) => {
                $scope.project.name = data.project.name;
                $scope.project.description = data.project.description;
                $scope.project.annotatedDescription = data.project.annotatedDescription;
                $scope.project.disabled = data.project.disabled;
            });
        };

        // Time to use for sorting branches
        $scope.getBranchTime = function (branch) {
            if (branch.latestBuild && branch.latestBuild.length > 0) {
                return branch.latestBuild[0].creation.time;
            } else {
                return branch.creation.time;
            }
        };

        // Sets a branch as favourite
        $scope.branchFavourite = function (branch) {
            if (branch.links._favourite) {
                ot.pageCall($http.put(branch.links._favourite)).then(loadProject);
            }
        };

        // Unsets a branch as favourite
        $scope.branchUnfavourite = function (branch) {
            if (branch.links._unfavourite) {
                ot.pageCall($http.put(branch.links._unfavourite)).then(loadProject);
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

        $scope.projectLabelFilter = (label) => {
            location.href = '#/home?label=' + otLabelService.formatLabel(label);
        };

        $scope.editProjectLabels = () => {
            if ($scope.project.links._labels) {
                const labelQuery = `{
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
                }`;
                otGraphqlService.pageGraphQLCall(labelQuery).then(resultLabels => {
                    return $modal.open({
                        templateUrl: 'app/dialog/dialog.project.labels.tpl.html',
                        controller: 'otDialogProjectLabels',
                        resolve: {
                            config: function () {
                                return {
                                    labels: resultLabels.labels,
                                    project: $scope.project,
                                    submit: function (labels) {
                                        const request = {
                                            labels: labels.filter(it => it.selected)
                                                .map(it => it.id)
                                        };
                                        return ot.call($http.put($scope.project.links._labels, request));
                                    }
                                };
                            }
                        }
                    }).result;
                }).then(loadProject);
            }
        };
    })
;