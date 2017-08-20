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
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;
        // Initial name filter
        $scope.branchNameFilter = '';

        // Loading the project and its whole information
        function loadProject() {
            $scope.loadingBranches = true;
            otGraphqlService.pageGraphQLCall("query ProjectView($projectId: Int) {\n" +
                "  projects(id: $projectId) {\n" +
                "    id\n" +
                "    name\n" +
                "    description\n" +
                "    disabled\n" +
                "    decorations {\n" +
                "      ...decorationContent\n" +
                "    }\n" +
                "    links {\n" +
                "      _self\n" +
                "      _createBranch\n" +
                "      _update\n" +
                "      _delete\n" +
                "      _permissions\n" +
                "      _clone\n" +
                "      _enable\n" +
                "      _disable\n" +
                "      _properties\n" +
                "      _extra\n" +
                "      _events\n" +
                "    }\n" +
                "    branches {\n" +
                "      id\n" +
                "      name\n" +
                "      disabled\n" +
                "      type\n" +
                "      decorations {\n" +
                "        ...decorationContent\n" +
                "      }\n" +
                "      links {\n" +
                "        _page\n" +
                "        _enable\n" +
                "        _disable\n" +
                "        _delete\n" +
                "      }\n" +
                "      latestBuild: builds(count: 1) {\n" +
                "        id\n" +
                "        name\n" +
                "      }\n" +
                "      promotionLevels {\n" +
                "        id\n" +
                "        name\n" +
                "        image\n" +
                "        _image\n" +
                "        promotionRuns(last: 1) {\n" +
                "          build {\n" +
                "            id\n" +
                "            name\n" +
                "          }\n" +
                "        }\n" +
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
                "}\n", {projectId: projectId}).then(function (data) {
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
                    // FIXME ot.viewActionsCommand($scope.project._actions),
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
    })
;