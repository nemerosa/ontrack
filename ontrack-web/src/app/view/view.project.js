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

        // Loading the branches
        function loadBranches() {
            $scope.loadingBranches = true;
            otGraphqlService.pageGraphQLCall("{\n" +
                "  projects(id: 1) {\n" +
                "    id\n" +
                "    name\n" +
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
                $scope.project = data.projects[0];
            }).finally(function () {
                $scope.loadingBranches = false;
            });
            // ot.call($http.get($scope.project._branchStatusViews)).then(function (branchStatusViewsResources) {
            //     $scope.branchStatusViewsResources = branchStatusViewsResources;
            //     $scope.branchStatusViews = branchStatusViewsResources.resources;
            //     // View commands
            //     view.commands = [
            //         {
            //             condition: function () {
            //                 return branchStatusViewsResources._create;
            //             },
            //             id: 'createBranch',
            //             name: "Create branch",
            //             cls: 'ot-command-branch-new',
            //             action: function () {
            //                 otStructureService.create(branchStatusViewsResources._create, "New branch").then(loadBranches);
            //             }
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._update;
            //             },
            //             id: 'updateProject',
            //             name: "Update project",
            //             cls: 'ot-command-project-update',
            //             action: function () {
            //                 otStructureService.update(
            //                     $scope.project._update,
            //                     "Update project"
            //                 ).then(loadProject);
            //             }
            //         },
            //         {
            //             id: 'searchBuild',
            //             name: "Search builds",
            //             cls: 'ot-command-project-search-builds',
            //             link: '/build-search/' + $scope.project.id
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._disable;
            //             },
            //             id: 'disableProject',
            //             name: "Disable project",
            //             cls: 'ot-command-project-disable',
            //             action: function () {
            //                 ot.pageCall($http.put($scope.project._disable)).then(loadProject);
            //             }
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._enable;
            //             },
            //             id: 'enableProject',
            //             name: "Enable project",
            //             cls: 'ot-command-project-enable',
            //             action: function () {
            //                 ot.pageCall($http.put($scope.project._enable)).then(loadProject);
            //             }
            //         }, {
            //             id: 'showDisabled',
            //             name: "Show all branches",
            //             cls: 'ot-command-show-disabled',
            //             condition: function () {
            //                 return !$scope.showDisabled;
            //             },
            //             action: function () {
            //                 $scope.showDisabled = true;
            //             }
            //         }, {
            //             id: 'hideDisabled',
            //             name: "Hide disabled branches",
            //             cls: 'ot-command-hide-disabled',
            //             condition: function () {
            //                 return $scope.showDisabled;
            //             },
            //             action: function () {
            //                 $scope.showDisabled = false;
            //             }
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._permissions;
            //             },
            //             id: 'permissionsProject',
            //             name: "Permissions",
            //             cls: 'ot-command-project-permissions',
            //             link: '/admin-project-acl/' + $scope.project.id
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._clone;
            //             },
            //             id: 'cloneProject',
            //             name: "Clone project",
            //             cls: 'ot-command-project-clone',
            //             action: function () {
            //                 otCopyService.cloneProject($scope.project).then(function (newProject) {
            //                     $state.go('project', {
            //                         projectId: newProject.id
            //                     });
            //                 });
            //             }
            //         },
            //         {
            //             condition: function () {
            //                 return $scope.project._delete;
            //             },
            //             id: 'deleteProject',
            //             name: "Delete project",
            //             cls: 'ot-command-project-delete',
            //             action: function () {
            //                 otAlertService.confirm({
            //                     title: "Deleting a project",
            //                     message: "Do you really want to delete the project " + $scope.project.name +
            //                         " and all its associated data?"
            //                 }).then(function () {
            //                     return ot.call($http.delete($scope.project._delete));
            //                 }).then(function () {
            //                     $state.go('home');
            //                 });
            //             }
            //         },
            //         ot.viewApiCommand($scope.project._self),
            //         ot.viewActionsCommand($scope.project._actions),
            //         ot.viewCloseCommand('/home')
            //     ];
            // }).finally(function() {
            //     $scope.loadingBranches = false;
            // });
        }

        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.title = projectResource.name;
                view.description = projectResource.description;
                view.decorationsEntity = projectResource;
                view.api = projectResource._self;
                // Loads the branches
                loadBranches();
            });
        }

        // Initialization
        loadProject();

        // Reload callback available in the scope
        $scope.reloadProject = loadProject;

        // Enabling a branch
        $scope.enableBranch = function (branch) {
            if (branch._enable) {
                ot.pageCall($http.put(branch._enable)).then(loadProject);
            }
        };

        // Disabling a branch
        $scope.disableBranch = function (branch) {
            if (branch._disable) {
                ot.pageCall($http.put(branch._disable)).then(loadProject);
            }
        };

        // Deleting a branch
        $scope.deleteBranch = function (branch) {
            otStructureService.deleteBranch(branch).then(loadProject);
        };
    })
;