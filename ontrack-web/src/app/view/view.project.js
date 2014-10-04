angular.module('ot.view.project', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.copy'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project', {
            url: '/project/{projectId}',
            templateUrl: 'app/view/view.project.tpl.html',
            controller: 'ProjectCtrl'
        });
    })
    .controller('ProjectCtrl', function ($scope, $stateParams, $state, $http, ot, otStructureService, otAlertService, otCopyService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;
        // Loading the branches
        function loadBranches() {
            ot.call($http.get($scope.project._branches)).then(function (branchCollection) {
                $scope.branchCollection = branchCollection;
                // Loading the branch status view for each branch
                angular.forEach(branchCollection.resources, function (branch) {
                    ot.call($http.get(branch._status)).then(function (branchStatusView) {
                        branch.branchStatusView = branchStatusView;
                    });
                });
                // View commands
                view.commands = [
                    {
                        condition: function () {
                            return branchCollection._create;
                        },
                        id: 'createBranch',
                        name: "Create branch",
                        cls: 'ot-command-branch-new',
                        action: function () {
                            otStructureService.create(branchCollection._create, "New branch").then(loadBranches);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project._update;
                        },
                        id: 'updateProject',
                        name: "Update project",
                        cls: 'ot-command-project-update',
                        action: function () {
                            otStructureService.update(
                                $scope.project._update,
                                "Update project"
                            ).then(loadProject);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project._disable;
                        },
                        id: 'disableProject',
                        name: "Disable project",
                        cls: 'ot-command-project-disable',
                        action: function () {
                            ot.pageCall($http.put($scope.project._disable)).then(loadProject);
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project._enable;
                        },
                        id: 'enableProject',
                        name: "Enable project",
                        cls: 'ot-command-project-enable',
                        action: function () {
                            ot.pageCall($http.put($scope.project._enable)).then(loadProject);
                        }
                    }, {
                        id: 'showDisabled',
                        name: "Show disabled items",
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
                    },
                    {
                        condition: function () {
                            return $scope.project._permissions;
                        },
                        id: 'permissionsProject',
                        name: "Permissions",
                        cls: 'ot-command-project-permissions',
                        link: '/admin-project-acl/' + $scope.project.id
                    },
                    {
                        condition: function () {
                            return $scope.project._clone;
                        },
                        id: 'cloneProject',
                        name: "Clone project",
                        cls: 'ot-command-project-clone',
                        action: function () {
                            otCopyService.cloneProject($scope.project).then(function (newProject) {
                                $state.go('project', {
                                    projectId: newProject.id
                                });
                            });
                        }
                    },
                    {
                        condition: function () {
                            return $scope.project._delete;
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
                                return ot.call($http.delete($scope.project._delete));
                            }).then(function () {
                                $state.go('home');
                            });
                        }
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.title = projectResource.name;
                view.description = projectResource.description;
                view.decorationsEntity = projectResource;
                // Loads the branches
                loadBranches();
            });
        }

        // Initialization
        loadProject();

        // Reload callback available in the scope
        $scope.reloadProject = loadProject;
    })
;